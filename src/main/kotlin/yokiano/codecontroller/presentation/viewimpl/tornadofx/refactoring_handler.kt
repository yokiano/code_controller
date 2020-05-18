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
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.stage.DirectoryChooser
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import progressCyclic
import tornadofx.*
import tornadofx.Stylesheet.Companion.hover
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

    val occurrencesList = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())
    var currentOccurrenceIndex = SimpleIntegerProperty(0)
    fun currentOccurrence(): MatchOccurrence? {
        return occurrencesList.getOrNull(currentOccurrenceIndex.value)
    }

    val totalOccurrences = SimpleIntegerProperty(0).apply {
        bind(occurrencesList.sizeProperty())
    }

    val approvedChanges = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())
    val dismissedChanges = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())

    // ------ diff pane
    val leftDiffPaneContent = SimpleStringProperty("")
    val rightDiffPaneContent = SimpleStringProperty("")
    lateinit var leftTextArea: TextArea
    lateinit var rightTextArea: TextArea
    lateinit var editButton: ToggleButton
    lateinit var commentsCheckbox: CheckBox
    val fileNameForNoMatch = "Couldn't find a match"
    val fileNameLabelStr = SimpleStringProperty(fileNameForNoMatch)
    val buttonsThatShouldDisable = ArrayList<Control>()

    // ------ Search Path Pane
    val supportedExtensions = arrayOf("kt")
    val searchTargetPathList = SimpleListProperty(ArrayList<SearchTargetPath>().asObservable()).apply {
//        runLater {
        this.add(SearchTargetPath(File("./")))
//        }
    }

    init {
        startRefactorOperation()
    }

    // ------ Params
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
                                paddingRight = 20.0
                                isVisible = false

                                val binding = stringBinding(approvedChanges.sizeProperty()) {
                                    if (this.value > 0) {
                                        (this@label as Label).isVisible = true
                                    }
                                    "${this.value} Approved"
                                }
                                bind(binding)
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
                        }
                        val approveAllButton = button("Approve All") {
                            buttonsThatShouldDisable.add(this)
                            shortcut("Ctrl+Shift+Alt+A")
                            action {
                                approvedChanges.addAll(occurrencesList)
                                occurrencesList.clear()
                                displayNextDiff()
                            }
                        }
                        val nextButton = button("Next") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                displayNextDiff()
                            }
                        }
                        val dismissButton = button("Dismiss") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                currentOccurrence()?.apply {
                                    dismissedChanges.add(this)
                                    occurrencesList.remove(this)
                                    displayNextDiff()
                                }
                            }
                        }
                        editButton = togglebutton("Edit", selectFirst = false) {
                            buttonsThatShouldDisable.add(this)
                            selectedProperty().onChange {
                                setEditable(it)
                            }
                        }
                        val openFileButton = button("Open File") {
                            buttonsThatShouldDisable.add(this)
                            action {
                                if (!Desktop.isDesktopSupported()) {
                                    return@action
                                }
                                currentOccurrence()?.let {
                                    Desktop.getDesktop().open(it.origFile)
                                }
                            }
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
                    tooltip("Selecting this will skip declarations that are commented. May cause performance degradation.")
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
                                    hgrow = Priority.ALWAYS
                                    tooltip(it.folder.absolutePath)
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
                        }
                        button("-") {
                            action {
                                listView.selectedItem?.let {
                                    searchTargetPathList.remove(it)
                                }
                            }
                        }

                        button("Refresh\nResults") {
                            action {
                                startRefactorOperation()
                            }
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
                button("Apply") {
                    shortcut("Ctrl+Shift+S")
                    action {
                        approvedChanges.groupBy { it.origFile }.entries.forEach { entry -> // Will traverse each group
                            val localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMy_Hmss"))
                            val backupFile =
                                File("${entry.key.parentFile}/ccBackup/${entry.key.name}.bkp.${localTime}").apply {
                                    mkdirs()
                                }
                            check(entry.key.copyTo(backupFile, true).exists()) {
                                "Couldn't Backup ${entry.key.name}."
                            }

                            applyChangesToFile(entry)
                        }

                        runLater {
                            close()
                            unit.stateProperty.value = CCUnitState.DEAD
                        }
                    }
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
        searchTargetPathList.forEach { path ->
            path.searchTask = runAsync(path.taskStatus) {

                val numOfFiles = path.folder.walk().filter { it.extension in supportedExtensions }
                    .count() // Needed for the progress bar calculation
                if (numOfFiles > 0) {
                    path.folder.walk().filter { it.extension in supportedExtensions }.forEachIndexed() { index, file ->
                        updateProgress(index.toLong(), numOfFiles.toLong())
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
            Regex("""((/\*(.|[\r\n])*?\*/)|[\s\t]*\/\/.*)""").findAll(originalFileText)
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

        if (isChanged) {
            if (isFirst && localList.size > 0) {
                localList.first().apply {
                    displayDiff(this.origFile, this.revisedFile, this.range)
                }
                return false
            }
        }
        return true
    }


    fun displayDiff(
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

    fun displayNextDiff(): Boolean {
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

    fun setEditable(isEditable: Boolean) {
        currentOccurrence()?.let {
            it.wasEditedManually = isEditable
            rightTextArea.isDisable = !isEditable
            rightTextArea.isEditable = isEditable
        }
        fun setDefStyle() {
            editButton.style {
                borderColor += MyStyle.refactorViewBorderColor

                backgroundColor += if (isEditable) {
                    c("#00aa0088")
                } else {
                    MyStyle.ALMOST_TRANSPARENT
                }
            }
        }
        setDefStyle()

        editButton.setOnMouseEntered {
            editButton.style {
                backgroundColor += if (isEditable) {
                    c("#00aa00dd")
                } else {
                    MyStyle.ALMOST_OPAQUE
                }
            }
        }

        editButton.setOnMouseExited {
            setDefStyle()
        }

    }

    fun cancelSearchTasks() {
        searchTargetPathList.forEach {
            if (it.isTaskInitialized()) {
                it.searchTask.cancel()
            }
        }
    }

    fun disableButtons() {
        buttonsThatShouldDisable.forEach {
            it.isDisable = true
        }
    }

    fun enableButtons() {
        buttonsThatShouldDisable.forEach {
            it.isDisable = false
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