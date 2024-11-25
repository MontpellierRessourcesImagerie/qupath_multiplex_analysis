//guiscript=true
import qupath.lib.gui.tools.MeasurementExporter
import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathRootObject
import qupath.lib.objects.PathDetectionObject
import ij.gui.GenericDialog


gd = GenericDialog("Multiplex Analysis Options")
gd.addTextAreas("DAPI, CD44v6, Ki67", null, 3, 8)
gd.showDialog()
if (gd.wasCanceled()) return
channels =  gd.getNextString();
channels = channels.replaceAll("\\s","")    // Remove whitespace
listOfChannels = channels.split(",")
print(channels)

/** OPTIONS, modify as necessary */
classesAndColours = ["DAPI": ColorTools.makeRGB(0,0,255), "Vimentin": ColorTools.makeRGB(0,255,0), "PTK7": ColorTools.makeRGB(255,0,0)]
setImageType('FLUORESCENCE');
setChannelNames(
     'DAPI',
     'CD44v6',
     'Ki67',
)

/** Setup */
def pathClasses = getQuPath().getAvailablePathClasses()
listOfClasses = []
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