package yokiano.codecontroller.presentation.viewimpl.tornadofx

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.text.FontPosture
import org.kodein.di.generic.instance
import org.kodein.di.tornadofx.kodein
import progressCyclic
import tornadofx.*
import java.io.File

data class MatchOccurrence(val origFile: File, val revisedFile: File, val range: IntRange, var dirty: Boolean = false)
class RefactoringHandler<T>(val unit: TUnit<T>) : View() {
    val driver: TornadoDriver by kodein().instance<TornadoDriver>()

    // ------ File System related
    val rootProjectFolder = File("./")
    val supportedExtensions = arrayOf("kt")


    val taskStatus = TaskStatus()

    val occurrencesList = SimpleListProperty(ArrayList<MatchOccurrence>().asObservable())
    var currentOccurrenceIndex = SimpleIntegerProperty(0)
    fun currentOccurrence(): MatchOccurrence {
        return occurrencesList.getOrElse(currentOccurrenceIndex.value) {
            println("Returned dummy for index ${currentOccurrenceIndex.value}")
            MatchOccurrence(createTempFile(), createTempFile(), 0..1) // dummy
        }
    }

    val totalOccurrences = SimpleIntegerProperty(0).apply {
        bind(occurrencesList.sizeProperty())
    }
    val approvedChanges = ArrayList<MatchOccurrence>()

    // ------ diff pane
    val leftDiffPaneContent = SimpleStringProperty("")
    val rightDiffPaneContent = SimpleStringProperty("")
    lateinit var leftTextArea: TextArea
    lateinit var rightTextArea: TextArea
    val fileNameLabelStr = SimpleStringProperty("")

    init {
        startRefactorOperation()
    }

    // ------ Params
    override val root: BorderPane = borderpane {
        title = "Refactor Declaration"
//        vgrow = Priority.ALWAYS
        addClass(MyStyle.ccWindow, MyStyle.refactoring_view)
        prefWidth = driver.primaryScreenBounds.width * 0.7
        prefHeight = driver.primaryScreenBounds.height * 0.4
        runLater {
            scene.window.centerOnScreen()
        }

        top {
            vbox {                                                                                              // TOP WRAPPER
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
                            label(totalOccurrences)
                            label(" Matches")
                        }
                    }
                }
                hbox {                                                                          // WRAPPER FOR BUTTONS AND DIFF
                    vbox {                                                                              // FILE NAME + DIFF PANE
                        style {
                            borderColor += MyStyle.refactorViewBorderColor
                        }
                        hgrow = Priority.ALWAYS
                        borderpane {                                                    // FILE NAME + (CURRENT REFACTOR / TOTAL REFACTOR LINES)
                            style {
//                                opacity = 0.8
                                backgroundColor += MyStyle.ALMOST_TRANSPARENT
                            }
                            left {
                                paddingAll = 4.0
                                label(fileNameLabelStr)
                            }
                            right {
                                hbox {
                                    label() {
                                        bind(integerBinding(currentOccurrenceIndex) { value + 1 })
                                    }
                                    label(" / ")
                                    label(totalOccurrences)
                                }
                            }
                        }
                        hbox {                                                                              // DIFF
                            hgrow = Priority.ALWAYS
                            textarea(leftDiffPaneContent) {
                                leftTextArea = this
//                                vgrow = Priority.ALWAYS
                                isEditable = false
                                isDisable = true
                                isWrapText = false
                                this.isFocusTraversable = false

                                runLater {
                                    maxHeight = this@borderpane.height * 0.6
                                    opacity = 1.0
                                }
                            }
                            textarea(rightDiffPaneContent) {
                                rightTextArea = this
//                                vgrow = Priority.ALWAYS
                                isEditable = false
                                isDisable = true
                                isWrapText = false
                                runLater {
                                    maxHeight = this@borderpane.height * 0.6
                                    opacity = 1.0
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
                    vbox(2.0) {                                                                              // BUTTONS
                        paddingLeft = 5.0
                        button("Approve") {
                            action {
                                currentOccurrence().apply {
                                    if (dirty) {
                                        revisedFile.writeText(rightDiffPaneContent.value)
                                    }

                                    approvedChanges.add(this)
                                    occurrencesList.remove(this)
                                    displayNextDiff()

                                }
                            }
                        }
                        button("Approve All") {
                            approvedChanges.addAll(occurrencesList)
                            occurrencesList.clear()
                        }
                        button("Next") {
                            action {
                                displayNextDiff()
                            }
                        }
                        button("Dissmis") {
                            action {
                                occurrencesList.removeAt(currentOccurrenceIndex.value)
                                displayNextDiff()
                            }

                        }
                        button("Edit") {
                            action {
                                currentOccurrence().dirty = true
                                rightTextArea.isDisable = false
                                rightTextArea.isEditable = true
                            }
                        }
                        button("Open File") {
                        }

                        minWidth = Region.USE_PREF_SIZE
                        runLater {
                            children.forEach {
                                (it as Button).minWidth = this.width

                            }
                        }
                    }
                }
            }
        }

        center {
            // --- Search Paths
            vbox {
                paddingTop = 30.0
                paddingBottom = 2.0
                label("Search Paths:") {
                    alignment = Pos.TOP_LEFT
                }

                hbox {
                    hgrow = Priority.ALWAYS
                    // ------ PathBox
                    scrollpane {
                        hgrow = Priority.ALWAYS
//                            vgrow = Priority.ALWAYS
                        style {
                            borderColor += MyStyle.refactorViewBorderColor
                        }
                        stackpane {
                            alignment = Pos.CENTER_LEFT
                            val pathLabel = label {
                                val absPath = rootProjectFolder.absolutePath
                                val charLimit = 100
                                text = if (absPath.length > charLimit) {
                                    "..." + absPath.takeLast(charLimit)
                                } else {
                                    absPath
                                }
                            }
                            progressbar(taskStatus.progress) {
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
                            }
                        }
                    }
                    flowpane {
                        orientation = Orientation.VERTICAL
                        paddingLeft = 5.0
                        button("+")
                        button("-")
                        button("refresh")
                    }
                }
            }
        }

        bottom {
            hbox(2.0) {
                alignment = Pos.BASELINE_RIGHT
                button("Apply") {

                    action {

                        occurrencesList.forEach {

                            val backupFile = File("${it.origFile.parentFile}/ccBackup/${it.origFile.name}.bkp").apply {
                                mkdirs()
                            }

                            check(it.origFile.copyTo(backupFile, true).exists()) {
                                "Couldn't Backup ${it.origFile.name}."
                            } // Should throw exception in case couldn't backup.

                            check(it.revisedFile.copyTo(it.origFile, true).exists()) {
                                "Failed to overwrite file."
                            }

//                            check(it.origFile.delete() && it.revisedFile.renameTo(file)) { "failed to replace file" }
//                            it.origFile.

                        }

                    }
                }
                button("Cancel") {
                    val file = File("src/main/kotlin/refactor_test.kt")
                    file.writeText("ASDFG")

//                    close()
                }
            }
        }
    }

    private fun startRefactorOperation() {

        var isFirst = true
        val task = runAsync(taskStatus) {
            val numOfFiles = File("./").walk().filter { it.extension in supportedExtensions }.count()
            File("./").walk().filter { it.extension in supportedExtensions }.forEachIndexed() { index, file ->
//                println("it.name = ${file.name}")
                updateProgress(index.toLong(), numOfFiles.toLong())
                updateMessage("Finished $index files out of $numOfFiles")
                isFirst = matchRegex(file, isFirst)
            }
        } ui {
            println("Finished File Scan")
        }
    }

    private fun matchRegex(originalFile: File, isFirst: Boolean): Boolean {
        var isChanged = false

        val localList = ArrayList<MatchOccurrence>() // workAround as the global list can be modified only from FX thread.
/*
        val regexPattern =
            Regex("""(\w+\.)?(${unit.getDeclarationString()})\((?:[^()]|(\3))*\) *(\{(?:[^{}]++|(\4))*\})?""")
*/
        val regexPattern =
            Regex("""(\w+\.)?(${unit.getDeclarationString()})\((?:.|(\3))*?\) *(\{(?:[^{}]++|(\4))*\})?""")
        val regexID = Regex(""""${unit.id}"""")

        // TODO - disregard comments? https://stackoverflow.com/questions/14975737/regular-expression-to-remove-comment
        // TODO - add button to mark the diff again after focus is lost.

        regexPattern.findAll(originalFile.readText()).let { // Will create a sequence with all the matches in the file.
            it.forEach { matchResult ->
                regexID.find(matchResult.value)?.let { // Will match the controller for the specific ID
                    isChanged = true
                    val revisedFile = createTempFile()
                    revisedFile.writeText(
                        originalFile.readText().replaceRange(matchResult.range, unit.stringifiedValue())
                    )
                    runLater {
                        MatchOccurrence(originalFile, revisedFile, matchResult.range).apply {
                            localList.add(this)
                            occurrencesList.add(this)
                        }
                    }
//                    occurrenceList.add(MatchOccurrence(originalFile, revisedFile, matchResult.range))
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
        range: IntRange
    ) {

        runLater {
            selectTextInArea(orig, range, leftTextArea, leftDiffPaneContent)
            selectTextInArea(
                revised,
                IntRange(range.first, range.first + unit.stringifiedValue().length),
                rightTextArea,
                rightDiffPaneContent
            )

            fileNameLabelStr.value = orig.name
        }

        runLater(100.millis) {
            leftTextArea.scrollTop += 60.0
            rightTextArea.scrollTop += 60.0
        }
    }

    fun displayNextDiff() {
        currentOccurrenceIndex.value = currentOccurrenceIndex.value.progressCyclic(occurrencesList)
        val occ = occurrencesList.getOrElse(currentOccurrenceIndex.value) {

            MatchOccurrence(createTempFile(), createTempFile(), 0..1) // Dummy object
        }
        occ.apply {
            displayDiff(origFile, revisedFile, range)
        }
    }

    fun selectTextInArea(file: File, range: IntRange, textArea: TextArea, textContent: SimpleStringProperty) {
        var chars = 0
        var linesUntilMatch = -1
        var linesOfMatch = 1
        textContent.value = ""
        file.readLines().forEachIndexed { index, s ->
            chars += s.length + 2
            if (chars >= range.first && linesUntilMatch < 0) {
                linesUntilMatch = index + 1
            }
            if (chars >= range.first && chars < range.last) {
                linesOfMatch++
            }
            textContent.value += s + "\n"
        }

        val selectionStart = range.first - linesUntilMatch
        val selectionEnd = range.last - linesUntilMatch - (linesOfMatch - 3)
        textArea.selectRange(selectionStart, selectionEnd)
    }

/*
    fun displayDiff(orig: File, revised: File, range: IntRange) {
        var tempOrig = ""
        var tempRevised = ""

        val oldTag = "&&&&&&"
        val newTag = "%%%%%%"
        runLater {
            val diffGenerator = DiffRowGenerator.create()
//                .showInlineDiffs(true)
//                .inlineDiffByWord(true)
                .oldTag { _: Boolean? -> oldTag }
                .newTag { _: Boolean? -> newTag }
                .build()
            val rows = diffGenerator.generateDiffRows(
                orig.readLines(),
                revised.readLines()
            )

            rows.forEachIndexed { i, diffLine ->
                tempOrig += (diffLine.oldLine + "\n")
                tempRevised += (diffLine.newLine + "\n")
//                tempOrig += ("$i\t" + diffLine.oldLine + "\n")
//                tempRevised += ("$i\t" + diffLine.newLine + "\n")
            }

            selectChangedLines(oldTag, leftDiffPaneContent, tempOrig, leftTextArea)
            selectChangedLines(newTag, rightDiffPaneContent, tempRevised, rightTextArea)
        }
    }
*/


/*    fun selectChangedLines(tag: String, textAreaContent: SimpleStringProperty, text: String, textArea: TextArea) {
        val indexStart = text.indexOf(tag)
        val indexEnd = text.indexOf(tag, indexStart + 1)
//        val newText = text.replace(tag, "")
//        textAreaContent.value = newText
        textAreaContent.value = text
        textArea.selectRange(indexStart, indexEnd - tag.length)

        runLater(50.millis) {
            textArea.scrollTop += 80.0
        }

    }

 */
}

