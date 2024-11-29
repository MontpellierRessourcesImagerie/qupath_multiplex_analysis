//guiscript=true
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathRootObject
import qupath.lib.objects.PathDetectionObject
import qupath.lib.gui.scripting.QPEx
import ij.gui.GenericDialog


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
        def gd = new GenericDialog("Multiplex Analysis Options")
        gd.setInsets(0, 0, 0)
        gd.addMessage("channels:")
        gd.setInsets(0, 0, 0)
        gd.addTextAreas(CHANNELS, null, 3, 24)
        gd.setInsets(0, 0, 0)
        gd.addChoice("classifier: ", CLASSIFIERS, SELECTED_CLASSIFIER)
        gd.setInsets(0, 40, 0)
        gd.addMessage("Detection Parameters")
        gd.setInsets(0, 60, 0)
        gd.addMessage("Setup Parameters")
        gd.setInsets(0, 65, 0)
        gd.addChoice("detection channel: ", CHANNELS.split(","), DETECTION_CHANNEL)
        gd.showDialog()
        if (gd.wasCanceled()) return
        CHANNELS = gd.getNextText();
        CHANNELS = CHANNELS.replaceAll("\\s","")    // Remove whitespace
        SELECTED_CLASSIFIER = gd.getNextChoice();
        DETECTION_CHANNEL
    }
    print(["selected channels", CHANNELS.split(",")])
    print(["selected classifier", SELECTED_CLASSIFIER])
    
    /** OPTIONS, modify as necessary */
    def classesAndColours = ["DAPI": ColorTools.makeRGB(0,0,255), "Vimentin": ColorTools.makeRGB(0,255,0), "PTK7": ColorTools.makeRGB(255,0,0)]
    setImageType('FLUORESCENCE');
    setChannelNames(
        *CHANNELS.split(",")
    )
    
    
    /** Setup */
    def pathClasses = getQuPath().getAvailablePathClasses()
    def listOfClasses = []
    for (cc in classesAndColours) {
        listOfClasses.add(getPathClass(cc.key, cc.value))
    }    
    pathClasses.addAll(listOfClasses)
    createFullImageAnnotation(true) 
    
    /** Run the analysis on the project */
    runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', 
        '{"detectionImage":"DAPI", \
        "requestedPixelSizeMicrons":0.5, \
        "backgroundRadiusMicrons":8.0, \
        "backgroundByReconstruction":true, \
        "medianRadiusMicrons":0.0, \
        "sigmaMicrons":1.5, \
        "minAreaMicrons":10.0, \
        "maxAreaMicrons":400.0, \
        "threshold":100.0, \
        "watershedPostProcess":true, \
        "cellExpansionMicrons":5.0, \
        "includeNuclei":true, \
        "smoothBoundaries":true, \
        "makeMeasurements":true}')
    runObjectClassifier("composite_CD44_Ki67")
    
    
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