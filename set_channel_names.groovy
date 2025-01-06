import java.awt.Color
import qupath.lib.scripting.QP
import qupath.lib.gui.scripting.QPEx
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import ij.gui.GenericDialog

channelsFileName = "channel_names.txt"
projectFile = new File(QPEx.getQuPath().getProject().getPath().toString())
projectFolder = projectFile.getParentFile()

def batchIndex = getProperty(ScriptAttributes.BATCH_INDEX)
setNamesOfChannels(batchIndex)

def setNamesOfChannels(batchIndex) {
    def namesOfChannels = "DAPI, CD44v6, Ki67"
    def hexColors = "#0000FF, #00FF00, #FF0000"
    namesOfChannels = namesOfChannels.replaceAll("\\s","")
    hexColors = hexColors.replaceAll("\\s","")
    def file = new File(projectFolder, channelsFileName)
    if (!file.exists()) {
        writeChannelNames(namesOfChannels+"\n"+hexColors)
    }
    fileContent = new File(projectFolder, channelsFileName).text.split("\n") 
    namesOfChannels = fileContent[0].replaceAll("\\s","")
    hexColors = fileContent[1].replaceAll("\\s","")
    if (batchIndex < 1) {
        def gd = new GenericDialog("Set Channel Names")
        gd.addMessage("channels/colors")
        gd.setInsets(0, 20, 0)
        gd.addTextAreas(namesOfChannels, hexColors, 2, 32)
        gd.showDialog()
        if (gd.wasCanceled()) return false 
        namesOfChannels = gd.getNextText()
        namesOfChannels = namesOfChannels.replaceAll("\\s","")    // Remove whitespace
        hexColors = gd.getNextText()
        hexColors = hexColors.replaceAll("\\s","")
        writeChannelNames(namesOfChannels+"\n"+hexColors)
    }
    QP.setChannelNames(*namesOfChannels.split(","))
    if (!hexColors.startsWith("#")) {
        return true
    }
    hexColorsList = hexColors.split(",")
    transformedColors = []  
    for (color in hexColorsList) {
      colorObject = Color.decode(color)
      colorInt = QP.makeRGB(colorObject.getRed(), colorObject.getGreen(), colorObject.getBlue())
      transformedColors = transformedColors + colorInt
    }
    QP.setChannelColors(*transformedColors)
    return true
}

def writeChannelNames(namesOfChannels) {
    try {
        Files.writeString(new File(projectFolder, channelsFileName).toPath(),
                          namesOfChannels,
                          StandardCharsets.UTF_8
        )
    }
    catch (IOException ex) {
        System.out.print("Could not save the channel names!")
    }
}
    
