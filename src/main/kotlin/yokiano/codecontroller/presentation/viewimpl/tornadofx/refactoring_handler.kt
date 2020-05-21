@file:Suppress("RegExpRedundantEscape")

package yokiano.codecontroller.presentation.viewimpl.tornadofx

import com.github.difflib.DiffUtils
import com.github.difflib.patch.PatchFailedException
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.DirectoryChooser
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import progressCyclic
import tornadofx.*
import yokiano.codecontroller.domain.CCUnitState
import java.awt.Desktop
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class MatchOccurrence(
    val origFile: File,
    val revisedFile: File,
    val range: IntRange,
    var wasEditedManually: Boolean = false
)

class SearchTargetPath(val folder: File) {
    lateinit var searchTask: Task<Unit>
    val taskStatus = TaskStatus()

    fun isTaskInitialized() = this::searchTask.isInitialized
}

class RefactoringHandler<T>(val unit: TUnit<T>) : View() {
    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    private val occurrencesList = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())
    private var currentOccurrenceIndex = SimpleIntegerProperty(0)
    private fun currentOccurrence(): MatchOccurrence? {
        return occurrencesList.getOrNull(currentOccurrenceIndex.value)
    }

    private val totalOccurrences = SimpleIntegerProperty(0).apply {
        bind(occurrencesList.sizeProperty())
    }

    private val approvedChanges = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())
    private val dismissedChanges = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())

    // ------ diff pane
    private val leftDiffPaneContent = SimpleStringProperty("")
    private val rightDiffPaneContent = SimpleStringProperty("")
    private lateinit var leftTextArea: TextArea
    private lateinit var rightTextArea: TextArea
    private lateinit var editButton: ToggleButton
    private lateinit var commentsCheckbox: CheckBox
    private val fileNameForNoMatch = "Couldn't find a match"
    private val fileNameLabelStr = SimpleStringProperty(fileNameForNoMatch)
    private val buttonsThatShouldDisable = ArrayList<Control>()

    // ------ Search Path Pane
    private val supportedExtensions = arrayOf("kt")
    private val searchTargetPathList = SimpleListProperty(ArrayList<SearchTargetPath>().asObservable())

    init {
        loadPreferences()
        startRefactorOperation()
    }

    // ------ Params
    val BACKUP_CYCLIC_LIMIT = 10


    override val root: BorderPane = borderpane {
        title = "Refactor Declaration"
        vgrow = Priority.ALWAYS
        addClass(MyStyle.ccWindow, MyStyle.refactoring_view)
        prefWidth = driver.primaryScreenBounds.width * 0.7
        prefHeight = driver.primaryScreenBounds.height * 0.4

        runLater {
            scene.window.centerOnScreen()
            currentStage?.minHeight = this.boundsInParent.height + 100.0
        }

        top {
            vbox(3.0) {                                                                                              // TOP WRAPPER
                borderpane {                                                                            // HEAD LINE / TITLE + MATCH COUNT
                    opacity = 0.7
//                    paddingBottom = 2.0
                    left {
                        paddingBottom = 2.0
                        label("Match results for \"${unit.id}\"") {
                            style {
                                fontStyle = FontPosture.ITALIC
                            }
                        }
                    }
                    right {
                        hbox {
                            style {
                                fontStyle = FontPosture.ITALIC
                            }

                            label() {
                                val binding = integerBinding(
                                    totalOccurrences,
                                    dismissedChanges.sizeProperty(),
                                    approvedChanges.sizeProperty()
                                ) {
                                    this.value + dismissedChanges.size + approvedChanges.size
                                }
                                bind(binding)
                            }
                            label(" Total Matches")
                        }
                    }
                }
                hbox {                                                                          // WRAPPER FOR BUTTONS AND DIFF
                    vbox {                                                                              // FILE NAME + DIFF PANE
                        style {
                            borderColor += MyStyle.refactorViewBorderColor
                        }
                        hgrow = Priority.ALWAYS

                        borderpane {                                                             //before after row
                            paddingHorizontal = 400.0
                            style {
//                                opacity = 0.8
                                backgroundColor += MyStyle.ALMOST_TRANSPARENT
                            }
                            left {
                                label("Before")
                            }
                            center {                                            // The file name and file counter
                                hbox(20.0) {
                                    alignment = Pos.CENTER
                                    paddingAll = 4.0
                                    label(fileNameLabelStr) {
                                        tooltip {
                                            style {
                                                fontScale = 1.5
                                            }
                                            setOnShowing {
                                                text = currentOccurrence()?.origFile?.absolutePath ?: ""
                                            }
                                        }
                                    }


                                    hbox {                                      // File Counter
                                        label {
                                            bind(integerBinding(currentOccurrenceIndex, totalOccurrences) {
                                                if (totalOccurrences.value > 0) {
                                                    value + 1
                                                } else {
                                                    0
                                                }
                                            })

                                        }
                                        label(" / ")
                                        label(totalOccurrences)
                                    }

                                }
                            }
                            right {
                                label("After")
                            }

                            children.forEach {
                                it.style {
                                    opacity = 0.7
                                    fontStyle = FontPosture.ITALIC
                                }
                            }
                        }
                        hbox {                                                                              // DIFF
                            hgrow = Priority.ALWAYS
                            runLater {
                                minHeight = this@borderpane.height * 0.45
                            }
                            textarea(leftDiffPaneContent) {
                                addClass(MyStyle.leftTextArea)
                                leftTextArea = this
                            }
                            textarea(rightDiffPaneContent) {
                                addClass(MyStyle.rightTextArea)
                                rightTextArea = this
                            }

                            children.forEach {
                                (it as TextArea).apply {
                                    vgrow = Priority.ALWAYS
                                    hgrow = Priority.ALWAYS
                                    isEditable = false
                                    isDisable = true
                                    isWrapText = false

                                    runLater {
                                        maxHeight = this@borderpane.height * 0.6
                                        opacity = 1.0
                                    }
                                }
                            }
                            setOnScroll {
                                when {
                                    rightTextArea.boundsInParent.contains(it.x, it.y) -> {
                                        rightTextArea.scrollTop -= it.deltaY
                                        rightTextArea.scrollLeft -= it.deltaX
                                    }
                                    leftTextArea.boundsInParent.contains(it.x, it.y) -> {
                                        leftTextArea.scrollTop -= it.deltaY
                                        leftTextArea.scrollLeft -= it.deltaX
                                    }
                                }
                            }
                        }
                    }
                    vbox(3.0) {                                                                              // BUTTONS
                        paddingLeft = 5.0

                        button("Approve") {
                            addClass(MyStyle.greenButton)
                            buttonsThatShouldDisable.add(this)
                            shortcut("Ctrl+Shift+A")
                            action {
                                currentOccurrence()?.apply {
                                    if (wasEditedManually) {
                                        revisedFile.writeText(rightDiffPaneContent.value)
                                    }
                                    approvedChanges.add(this)
                                    occurrencesList.remove(this)
                                    displayNextDiff()

                                }
                            }
                            tooltip("Approve the current refactoring suggestion.\n(Ctrl+Shift+A)")
                        }
                        button("Approve All") {
                            buttonsThatShouldDisable.add(this)
                            shortcut("Ctrl+Shift+Alt+A")
                            action {
                                approvedChanges.addAll(occurrencesList)
                                occurrencesList.clear()
                                displayNextDiff()
                            }
                            tooltip("Approve ALL refactoring suggestions.\nWARNING - Perform with care.\n(Ctrl+Shift+Alt+A)")
                        }
                        button("Next") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                displayNextDiff()
                            }
                            tooltip("Display the next refatoring suggestion")
                        }
                        button("Dismiss") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                currentOccurrence()?.apply {
                                    dismissedChanges.add(this)
                                    occurrencesList.remove(this)
                                    displayNextDiff()
                                }
                            }
                            tooltip("Dismiss the current refactoring suggestion")
                        }
                        editButton = togglebutton("Edit", selectFirst = false) {
                            buttonsThatShouldDisable.add(this)
                            selectedProperty().onChange {
                                setEditable(it)
                            }
                            tooltip("Edit the right side according to what it should be")
                        }
                        button("Open File") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                if (!Desktop.isDesktopSupported()) {
                                    return@action
                                }
                                currentOccurrence()?.let {
                                    Desktop.getDesktop().open(it.origFile)
                                }
                            }
                            tooltip("Open the displayed file in the OS default text editor")
                        }

                        minWidth = Region.USE_PREF_SIZE
                        runLater {
                            children.forEach {
                                when (it) {
                                    is ButtonBase -> {
                                        it.minWidth = this.width
                                        it.isDisable = true
                                    }
                                    else -> {
                                    }
                                }
                            }
                        }
                    }
                }
                commentsCheckbox = checkbox("Exclude comments") {

                    isSelected = false
                    tooltip("Selecting this will skip declarations that are commented.")
                }
            }
        }

        center {                                    // ------ Search Target Paths
            vgrow = Priority.NEVER
            lateinit var listView: ListView<SearchTargetPath>
            // --- Search Paths
            vbox {
                paddingTop = 30.0
                paddingBottom = 2.0
                label("Search Paths:") {
                    style {
                        fontStyle = FontPosture.ITALIC
                        alignment = Pos.TOP_LEFT
                        opacity = 0.7
                    }
                }

                hbox {
                    listView = listview(searchTargetPathList) {
                        style {
                            borderColor += MyStyle.refactorViewBorderColor
                        }
                        runLater {
                            prefWidthProperty().bind((this@hbox).widthProperty())
                        }
                        addClass(MyStyle.pathListView)
                        cellFormat {
                            graphic = stackpane {
                                alignment = Pos.CENTER_LEFT
                                val pathLabel = label {
                                    text = it.folder.absolutePath
                                    tooltip(it.folder.absolutePath)
                                }
                                progressbar(it.taskStatus.progress) {
                                    isMouseTransparent = true
                                    prefWidthProperty().bind(doubleBinding(pathLabel.widthProperty()) { value * 1.05 })
                                    prefHeightProperty().bind(
                                        doubleBinding(
                                            pathLabel.heightProperty()
                                        ) { value * 1.2 })
                                    stackpaneConstraints {
                                        marginLeft = -3.0
                                    }
                                    addClass(MyStyle.taskProgress)
                                }
                            }
                        }
                    }
                    vbox(3.0) {
                        paddingLeft = 5.0
                        button("+") {
                            action {
                                DirectoryChooser().apply {
                                    initialDirectory = File("./")
                                    showDialog(currentWindow)?.let {
                                        searchTargetPathList.add(SearchTargetPath(it))
                                    }
                                }
                            }
                            tooltip("Add a search target folder")
                        }
                        button("-") {
                            action {
                                listView.selectionModel.selectedIndices.forEach {
                                    searchTargetPathList.removeAt(it)
                                    listView.selectionModel.select(if (it <= 0) 0 else it - 1)
                                }
                            }
                            tooltip("Remove the selected search target folder")
                        }

                        button("Refresh\nResults") {
                            action {
                                startRefactorOperation()
                            }
                            tooltip("Refresh the matching results.\nWill reset the currently approved changes")
                        }
                        minHeight = Region.USE_PREF_SIZE
                        minWidth = Region.USE_PREF_SIZE

                        runLater {
                            children.forEach {
                                if (it is Button) {
                                    it.minWidth = this.width
                                }
                            }
                        }
                    }
                }
            }
        }


        bottom {
            hbox(2.0) {
                paddingTop = 40.0
                alignment = Pos.BASELINE_RIGHT
                label() {
                    paddingRight = 10.0
                    style {
                        textFill = c("#00aa00aa")
                        fontWeight = FontWeight.BOLD
                        fontStyle = FontPosture.ITALIC
                    }
                    val binding = stringBinding(approvedChanges.sizeProperty()) {
                        "${this.value} Changes Approved"
                    }
                    bind(binding)
                }
                button("Remove and Refactor") {
                    addClass(MyStyle.greenButton)
                    shortcut("Ctrl+Shift+S")
                    action {
                        applyApprovedChanges()
                        runLater {
                            savePreferences()
                            close()
                            unit.stateProperty.value = CCUnitState.DEAD
                        }
                    }
                    tooltip("Apply the approved refactor changes,\nand remove the control from the controls panel\n(Ctrl+Shift+S)")
                }
                button("Refactor") {
                    shortcut("Ctrl+Shift+Alt+S")
                    action {
                        applyApprovedChanges()
                        runLater {
                            savePreferences()
                            close()
                        }
                    }
                    tooltip("Apply the approved refactor changes\n(Ctrl+Shift+Alt+S)")
                }
                button("Cancel") {
                    action {
                        cancelSearchTasks()
                        close()
                    }
                }
            }
        }
    }

    private fun initComponents() {
        currentOccurrenceIndex.value = 0
        fileNameLabelStr.value = fileNameForNoMatch
        cancelSearchTasks()
        occurrencesList.clear()
        approvedChanges.clear()
        dismissedChanges.clear()
        leftDiffPaneContent.value = ""
        rightDiffPaneContent.value = ""
    }

    private fun startRefactorOperation() {
        initComponents()
        var isFirst = true
        searchTargetPathList.forEach { searchTarget ->
            searchTarget.searchTask = runAsync(searchTarget.taskStatus) {

                val numOfFiles = searchTarget.folder.walk().filter { it.extension in supportedExtensions }
                    .count() // Needed for the progress bar calculation
                if (numOfFiles > 0) {
                    searchTarget.folder.walk().filter { it.extension in supportedExtensions }
                        .forEachIndexed() { index, file ->
                            runLater {
                                updateProgress(index.toLong(), numOfFiles.toLong())
                            }
//                        updateMessage("Finished $index files out of $numOfFiles") // might be good later to give status on search progression with numbers.
                            isFirst = matchRegex(file, isFirst)
                        }
                } else {
                    updateProgress(1, 1)
                }
            } ui {
            }
        }
    }

    private fun matchRegex(originalFile: File, isFirst: Boolean): Boolean {
        var isChanged = false

        val localList =
            ArrayList<MatchOccurrence>() // workAround as the global list can be modified only from FX thread.
/*
        val regexPattern =
            Regex("""(\w+\.)?(${unit.getDeclarationString()})\((?:[^()]|(\3))*\) *(\{(?:[^{}]++|(\4))*\})?""")
*/
        val regexPattern =
            Regex("""(\w+\.)?(${unit.getDeclarationString()})\((?:.|(\3))*?\) *(\{(?:[^{}]++|(\4))*\})?""")
        val regexID = Regex(""""${unit.id}"""")

        // TODO - disregard comments? https://stackoverflow.com/questions/14975737/regular-expression-to-remove-comment
        // TODO - add button to mark the diff again after focus is lost.
        val originalFileText = originalFile.readText().replace("\r", "")

        val commentsMatches = if (commentsCheckbox.isSelected) {
            Regex("""((/\*(.|[\r\n])*?\*/)|[\s\t]*//.*)""").findAll(originalFileText)
        } else {
            emptySequence()
        }

        regexPattern.findAll(originalFileText).let { // Will create a sequence with all the matches in the file.
            it.forEach mr@{ matchResult ->
                regexID.find(matchResult.value)?.let { // Will match the controller for the specific ID

                    commentsMatches.forEach { commentMatch -> // Return if the match is inside a comment. will skip if checkbox is not selected.
                        if (matchResult.range.first in commentMatch.range && matchResult.range.last in commentMatch.range) {
                            return@mr
                        }
                    }

                    isChanged = true
                    val revisedFile = createTempFile()
                    revisedFile.writeText(
                        originalFileText.replaceRange(matchResult.range, unit.stringifiedValue())
                    )
                    MatchOccurrence(originalFile, revisedFile, matchResult.range).apply {
                        localList.add(this)
                        runLater {
                            occurrencesList.add(this)
                        }
                    }
                }
            }
        }
/*
        revisedFile.printWriter().use { writer ->
            var lineNum = 0
            originalFile.forEachLine { line ->
                val newLine = regexPattern.find(line)?.let { matchResult ->
                    regexID.find(matchResult.value)?.let {
                        isChanged = true
                        println("$lineNum fileName:${originalFile.name}:: matchResult.value = ${matchResult.value}")
                        println("$lineNum groups = ${matchResult.groupValues}")
                        println("unit.stringifiedValue() = ${unit.stringifiedValue()}")
                        matches.add(matchResult.range)
                        line.replace(regexPattern, unit.stringifiedValue())
                        // TODO - add occurrence to list
                    } ?: line
                } ?: line // if regex was not matched leave the line as is.
                writer.println(newLine)
                lineNum++
            }
        }
*/

        if (isChanged && isFirst && localList.size > 0) {
            localList.first().apply {
                displayDiff(this.origFile, this.revisedFile, this.range)
            }
            return false
        }
        return true
    }


    private fun displayDiff(
        orig: File,
        revised: File,
        range: IntRange,
        isEmpty: Boolean = false
    ) {

        runLater {
            selectTextInArea(orig.readText(), range, leftTextArea, leftDiffPaneContent)
            selectTextInArea(
                revised.readText(),
                IntRange(range.first, range.first + unit.stringifiedValue().length),
                rightTextArea,
                rightDiffPaneContent
            )

            if (!isEmpty) {
                fileNameLabelStr.value = orig.name
                enableButtons()
            } else {
                fileNameLabelStr.value = fileNameForNoMatch
                disableButtons()
            }
        }


    }

    private fun displayNextDiff(): Boolean {
        currentOccurrenceIndex.value = currentOccurrenceIndex.value.progressCyclic(occurrencesList)
        setEditable(false)
        currentOccurrence()?.let {
            displayDiff(it.origFile, it.revisedFile, it.range)
            return true
        } ?: displayDiff(createTempFile(), createTempFile(), 0..1, true)
        return false
    }

    private fun selectTextInArea(
        fileText: String,
        range: IntRange,
        textArea: TextArea,
        textContent: SimpleStringProperty
    ) {
        textArea.scrollTop = 0.0
        textContent.value = ""
        textContent.value = fileText
        textArea.selectRange(range.first, range.last + 1)
        runLater(100.millis) {
            textArea.scrollTop += textArea.height * 0.5
        }
/*

        // currently depracated because decided to remove CR ("\r") chars before arriving here.
        var chars = 0
        var linesUntilMatch = -1
        var linesOfMatch = 1
        fileText.readLines().forEachIndexed { index, s ->
            chars += s.length
            if (chars >= range.first && linesUntilMatch < 0) {
                linesUntilMatch = index + 1
            }
            if (chars >= range.first && chars < range.last) {
                linesOfMatch++
            }
            textContent.value += s + "\n"


//        val selectionStart = range.first
//        val selectionEnd = range.last + 1
//        val selectionStart = range.first - linesUntilMatch
//        val selectionEnd = range.last - linesUntilMatch - (linesOfMatch - 3)
//        textArea.selectRange(selectionStart, selectionEnd)

 */
    }

    private fun setEditable(isEditable: Boolean) {
        currentOccurrence()?.let {
            it.wasEditedManually = isEditable
            rightTextArea.isDisable = !isEditable
            rightTextArea.isEditable = isEditable
        }
        fun setDefStyle() {
            editButton.style {
                borderColor += MyStyle.refactorViewBorderColor

                backgroundColor += if (isEditable) {
                    c("#00aa0044")
                } else {
                    MyStyle.ALMOST_TRANSPARENT
                }
            }
        }
        setDefStyle()

        editButton.setOnMouseEntered {
            editButton.style {
                backgroundColor += if (isEditable) {
                    c("#00aa0088")
                } else {
                    MyStyle.ALMOST_OPAQUE
                }
            }
        }

        editButton.setOnMouseExited {
            setDefStyle()
        }

    }

    private fun cancelSearchTasks() {
        searchTargetPathList.forEach {
            if (it.isTaskInitialized()) {
                it.searchTask.cancel()
            }
        }
    }

    private fun disableButtons() {
        buttonsThatShouldDisable.forEach {
            it.isDisable = true
        }
    }

    private fun enableButtons() {
        buttonsThatShouldDisable.forEach {
            it.isDisable = false
        }

    }

    // Returns false if backup wasn't successful.
    private fun backupFile(file: File): Boolean {
        val localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yMd_Hmss"))

        try {
            val backupFile =
                File("${file.parentFile}/ccBackup/${file.name}.bkp.${localTime}").apply {
                    mkdirs()
                }
            if (!file.copyTo(backupFile, true).exists()) {
                println("Couldn't Backup ${file.name}. Skipping changes for this file.")
                return false
            }

            val fileLists = backupFile.parentFile.listFiles()?.filter { it.name.startsWith("${file.name}.bkp.") }?.sorted() ?: return false
            if (fileLists.size > BACKUP_CYCLIC_LIMIT) {
                val deletedFileName = fileLists.first().absolutePath
                if (fileLists.first().delete()) {
                    println("Discovered more than $BACKUP_CYCLIC_LIMIT backups for the same file. Deleting the oldest one - $deletedFileName")
                } else {
                    println("Discovered more than $BACKUP_CYCLIC_LIMIT backups for the same file but failed to delete the oldest one.")
                }
            }

            return true
        } catch (e: Exception) {
            println("Error in backup operation - ${e.message}")
            return false
        }
    }

    private fun applyApprovedChanges() {
        approvedChanges.groupBy { it.origFile }.entries.forEach { entry -> // Will traverse each group
            if (!backupFile(entry.key)) {
                println("Couldn't backup")
                return@forEach
            }

            applyChangesToFile(entry)
        }
    }

    private fun applyChangesToFile(entry: Map.Entry<File, List<MatchOccurrence>>) {
        val origLines = entry.key.readLines()
        var newLines = entry.key.readLines()
        entry.value.forEach {
            val diff = DiffUtils.diff(origLines, it.revisedFile.readLines())
            try {
                newLines = diff.applyTo(newLines)
            } catch (e: PatchFailedException) {
                println("Failed to apply patches for  \'${entry.key.absolutePath}\', aborting changes. ${e.message}")
                return@forEach
            }
        }
        entry.key.writeText(newLines.joinToString(System.lineSeparator()))
    }

    private fun loadPreferences() {
        with(GeneralConfig.config) {
            ConfigEntry::class.sealedSubclasses.forEach {
                applyConfigObj(this, it.objectInstance)
            }
        }
    }

    private fun applyConfigObj(config: ConfigProperties, entry: ConfigEntry?) {
        with(config) {
            when (entry) {
                ConfigEntry.ExcludeComments -> {
                    runLater {
                        commentsCheckbox.isSelected = boolean(entry.key)?.run { this } ?: false
                    }
                }
                ConfigEntry.SearchPathTargetList -> {
                    val reducedList = jsonArray(entry.key)?.mapNotNull { jsonVal ->
                        File(jsonVal.toString().removePrefix("\"").removeSuffix("\"")).let {
                            if (it.exists()) {
                                SearchTargetPath(it)
                            } else {
                                null
                            }
                        }
                    } ?: return

                    if (reducedList.isNotEmpty()) {
                        searchTargetPathList.clear()
                        searchTargetPathList.addAll(reducedList)
                    } else {
                        searchTargetPathList.add(SearchTargetPath(File("./")))
                    }
                }
                null -> {
                    println("Config Entry is null. not performing any operation.")
                }
            }
        }
    }

    private fun savePreferences() {
        with(GeneralConfig.config) {
            ConfigEntry::class.sealedSubclasses.forEach {
                val key = it.objectInstance?.key ?: return@forEach
                val value = configEntryToObj(it.objectInstance)?.run {
                    this
                } ?: return@forEach
                set(key to value)
            }
            save()
        }
    }

    private fun configEntryToObj(entry: ConfigEntry?): Any? {
        return when (entry) {
            ConfigEntry.ExcludeComments -> commentsCheckbox.isSelected
            ConfigEntry.SearchPathTargetList -> searchTargetPathList.map { "\"${it.folder.invariantSeparatorsPath}\"" }
            null -> null
        }
    }


    sealed class ConfigEntry(var key: String) {
        object ExcludeComments : ConfigEntry("exclude_comments")
        object SearchPathTargetList : ConfigEntry("search_path_targets")

    }


}


/*
class SearchablePathFragment : ListCellFragment<SearchTargetPath>() {


    init {
        println("INITING CELL. item = ${item}")
    }

    override val root = stackpane {
        alignment = Pos.CENTER_LEFT
        val pathLabel = label {
            runLater {
                item?.let {
                    println("Setting text = ${it.folder.name}")
//                    text = it.folder.absolutePath.putDotsAfter(100)
                    text = "textyyy"
                }
            }
        }
*/
/*        progressbar(taskStatus.progress) {
            prefWidthProperty().bind(doubleBinding(pathLabel.widthProperty()) { value * 1.05 })
            prefHeightProperty().bind(
                doubleBinding(
                    pathLabel.heightProperty()
                ) { value * 1.2 })
            stackpaneConstraints {
                marginLeft = -3.0
            }
            addClass(MyStyle.taskProgress)
            hgrow = Priority.ALWAYS
            tooltip(rootProjectFolder.absolutePath)
        }*//*

    }
}
*/
