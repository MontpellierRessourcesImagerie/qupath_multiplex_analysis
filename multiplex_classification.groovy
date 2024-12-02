//guiscript=true
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathRootObject
import qupath.lib.objects.PathDetectionObject
import qupath.lib.gui.scripting.QPEx
import ij.gui.GenericDialog
import qupath.lib.common.GeneralTools


CHANNELS = "DAPI, CD44v6, Ki67"
CHANNELS = CHANNELS.replaceAll("\\s","")    // Remove whitespace
DETECTION_CHANNEL = CHANNELS.split(",")[0]
PIXEL_SIZE = 0.5        // micron
BACKGROUND_RADIUS = 8   // micron 
OPENING_BY_RECONSTRUCTION = true
MEDIAN_FILTER_RADIUS = 0
SIGMA = 1.5             // micron
MIN_AREA = 10           // micron*micron
MAX_AREA = 400
THRESHOLD = 100
SPLIT_BY_SHAPE = true
CELL_EXPANSION = 5    // micron
INCLUDE_NUCLEUS = true
SMOOTH_BOUNDERIES = true
OUTPUT_PATH = null
DEFAULT_OUTPUT_FILENAME = 'measurements.tsv'
main()

def main() {
    def qupathGUI = QPEx.getQuPath()
    def currentProject = qupathGUI.getProject()
    CLASSIFIER_KEYS = currentProject.getObjectClassifiers().getNames()
    CLASSIFIERS = currentProject.getObjectClassifiers().getNames().toArray(new String[CLASSIFIER_KEYS.size()])
    SELECTED_CLASSIFIER = CLASSIFIERS[0]
    def batchIndex= getProperty(ScriptAttributes.BATCH_INDEX)
    print("Processing image " + (batchIndex + 1))
    def selectedChannels = ""
    def listOfChannels = []
    if (batchIndex<1) {
        ok = getOptionsFromUser()
        if (!ok) return;
    }
    print(["selected channels", CHANNELS.split(",")])
    print(["selected classifier", SELECTED_CLASSIFIER])
    
    setImageType('FLUORESCENCE');
    setChannelNames(
        *CHANNELS.split(",")
    )

    createFullImageAnnotation(true) 
    
    /** Run the analysis on the project */
    runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', 
        '{"detectionImage":'+DETECTION_CHANNEL+', \
        "requestedPixelSizeMicrons":'+PIXEL_SIZE+', \
        "backgroundRadiusMicrons":'+BACKGROUND_RADIUS+', \
        "backgroundByReconstruction":'+OPENING_BY_RECONSTRUCTION+', \
        "medianRadiusMicrons":'+MEDIAN_FILTER_RADIUS+', \
        "sigmaMicrons":'+SIGMA+', \
        "minAreaMicrons":'+MIN_AREA+', \
        "maxAreaMicrons":'+MAX_AREA+', \
        "threshold":'+THRESHOLD+', \
        "watershedPostProcess":'+SPLIT_BY_SHAPE+', \
        "cellExpansionMicrons":'+CELL_EXPANSION+', \
        "includeNuclei":'+INCLUDE_NUCLEUS+', \
        "smoothBoundaries":'+SMOOTH_BOUNDERIES+', \
        "makeMeasurements":true}')
    runObjectClassifier(SELECTED_CLASSIFIER)
    
    
    /** Export the measurements as a tsv-file */
    
    def project = getProject()
    def imagesToExport = project.getImageList()
    def separator = "\t"
    def columnsToInclude = new String[]{}
    def exportType = PathRootObject.class
    
    // Choose your *full* output path
    def outputPath = "M:/measurements.tsv"
    def outputFile = new File(outputPath)
    
    // Create the measurementExporter and start the export
    def exporter  = new MeasurementExporter()
                      .imageList(imagesToExport)            // Images from which measurements will be exported
                      .separator(separator)                 // Character that separates values
                      .includeOnlyColumns(columnsToInclude) // Columns are case-sensitive
                      .exportType(exportType)               // Type of objects to export
                      .filter(obj -> obj.getPathClass() == getPathClass("Tumor"))    // Keep only objects with class 'Tumor'
                      .exportMeasurements(outputFile)        // Start the export process
}    
    
def getProjectFolder() {
    def qupathGUI = QPEx.getQuPath()
    pro = qupathGUI.getProject()
    projectFile = new File(pro.getPath().toString())
    projectFolder = projectFile.getParentFile()
    return projectFolder
}

def getOptionsFromUser() {
    setOutputPath()
    def gd = new GenericDialog("Multiplex Analysis Options")
    gd.addMessage("Batch Parameters")
     
    gd.addMessage("channels:")
    gd.setInsets(0, 20, 0)
    gd.addTextAreas(CHANNELS, null, 2, 16)
    gd.addChoice("classifier: ", CLASSIFIERS, SELECTED_CLASSIFIER)
    gd.addFileField("output file: ", OUTPUT_PATH, 24)
    
    gd.addMessage("Detection Parameters (Setup)")
    gd.addChoice("detection channel: ", CHANNELS.split(","), DETECTION_CHANNEL)
    gd.addToSameRow()
    gd.addNumericField("pixel size (µm): ", PIXEL_SIZE)  
    
    gd.addMessage("Detection Parameters (Nucleus)")
    gd.addNumericField("background radius (µm): ", BACKGROUND_RADIUS)
    gd.addToSameRow()
    gd.addCheckbox("use opening by reconstruction", OPENING_BY_RECONSTRUCTION)
    gd.addNumericField("median filter radius (µm): ", MEDIAN_FILTER_RADIUS)
    gd.addToSameRow()
    gd.addNumericField("sigma (µm): ", SIGMA)
    gd.addNumericField("min. area (µm²)", MIN_AREA)
    gd.addToSameRow()
    gd.addNumericField("max. area (µm²)", MAX_AREA)
    gd.addMessage("Detection Parameters (Intensity)")
    gd.addNumericField("threshold: ", THRESHOLD)
    gd.addToSameRow()
    gd.addCheckbox("split by shape", SPLIT_BY_SHAPE)
    gd.addMessage("Detection Parameters (Cell)")
    gd.addNumericField("cell expansion (µm)", CELL_EXPANSION)
    gd.addToSameRow()
    gd.addCheckbox("include nucleus", INCLUDE_NUCLEUS)
    gd.addMessage("Detection Parameters (General)")
    gd.addCheckbox("smooth boundaries", SMOOTH_BOUNDERIES)
    gd.showDialog()
    if (gd.wasCanceled()) return false
    CHANNELS = gd.getNextText();
    CHANNELS = CHANNELS.replaceAll("\\s","")    // Remove whitespace
    SELECTED_CLASSIFIER = gd.getNextChoice()
    OUTPUT_PATH = gd.getNextString()
    DETECTION_CHANNEL = gd.getNextChoice()
    PIXEL_SIZE = gd.getNextNumber()
    BACKGROUND_RADIUS = gd.getNextNumber()
    OPENING_BY_RECONSTRUCTION = gd.getNextBoolean()
    MEDIAN_FILTER_RADIUS = gd.getNextNumber()
    SIGMA = gd.getNextNumber()
    MIN_AREA = gd.getNextNumber()
    MAX_AREA = gd.getNextNumber()
    THRESHOLD = gd.getNextNumber()        
    SPLIT_BY_SHAPE = gd.getNextBoolean()
    CELL_EXPANSION = gd.getNextNumber()
    INCLUDE_NUCLEUS = gd.getNextBoolean()
    SMOOTH_BOUNDERIES = gd.getNextBoolean()
    return true
}


def setOutputPath() {
    projectFolder = getProjectFolder()
    def qupathGUI = QPEx.getQuPath()
    def currentProject = qupathGUI.getProject()
    path = currentProject.getPath()
    name = GeneralTools.getNameWithoutExtension(new File(path.toString()))
    OUTPUT_PATH = new File(projectFolder, name + "_" + DEFAULT_OUTPUT_FILENAME).toString()
}