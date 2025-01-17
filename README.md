# Qupath Multiplex Analysis


<img align='left'  src="https://github.com/user-attachments/assets/1317dabe-5194-4380-8140-bff051d9c6a0" width='40%'/> 
<img align='top'  src="https://github.com/user-attachments/assets/50c6ce56-9dba-453d-8004-cbff2e487293" width='56%' /)


The scripts in this project allow to automatically run a multiplex analysis in qupath and add columns for the total numers of each class to the final results table.


## Installation


Download the files [CombinationClassCounter.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/CombinationClassCounter.groovy), [convert_table.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/convert_table.groovy), [multiplex_classification.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/multiplex_classification.groovy) and [set_channel_names.groovy](https://raw.githubusercontent.com/MontpellierRessourcesImagerie/qupath_multiplex_analysis/refs/heads/main/set_channel_names.groovy) and save them into the folder ``scripts`` of your qupath project.  


* The script ``convert_table.groovy`` will add the total numbers for each class to the results table
  * After having analyzed your images, open the script in the script editor (``Automate>Script Editor``).
  * Run the script via the command ``Run`` from the menu ``Run`` of the script editor (do not use ``Run for project``)
  * Select a results file in the tab-separated values (.tsv) format
  * The script will create a new tsv-file in the same folder as the input file


* The script ``set_channel_names.groovy`` allows to set the names and colors of the channels of the images in the project
  * Open the script in the script editor (``Automate>Script Editor``).
  * Run the script via the command ``Run for project`` from the menu ``Run`` of the script editor
   
* The script ``multiplex_classification.groovy`` allows to automatically run a multiplex analysis on all images of a project
  * Open the script in the script editor (``Automate>Script Editor``).
  * Run the script via the command ``Run for project`` from the menu ``Run`` of the script editor
 
## Step by step protocol


Before getting started, you might want to read the [multiplex analysis](https://qupath.readthedocs.io/en/stable/docs/tutorials/multiplex_analysis.html) part of the QuPath-documentation.


### 1. Create a project and import your images


Run the command ``File>Project...>Create Project``. A dialog will open from which you can create and/or select an empty folder, into which the project files will be saved. Open the folder containing your images in your system's filebrowser and drag them onto the ``Image list`` pane of your project in QuPath. On the import dialog, select the image type (for example ``Fluorescence``) and press the import button.


Depending on the number, type and size of your images, the import might take a while, since a multi-resolution image pyramid has to be created.


### 2. Setting channel names and colors

We will use a script to set the names and colors of the channels of all images in the project.

We will select the colors for the different channels and note their codes, so that we can use them in the script later. Open one of the images in the project. Open the ``Brightness & Contrast`` dialog (``Shift+c``). Double-click on a channel to open the ``channel properties``. Select a color for the channel and note the hex-code of the color (for example ``#994d66``). If you use one of the named colors, let the mouse-pointer hoover over the color for a moment to get the hex-code.

![image](https://github.com/user-attachments/assets/2e1dc1c4-156f-4f44-a207-a64bba8c3351)


Close the dialogs and close the image viewer by right-clicking into the image and choosing ``Multi-view...>Close viewer``. Run the script ``set_channel_names.groovy`` from the script editor (see above) using the ``Run for project`` command.

![image](https://github.com/user-attachments/assets/c0f5ca40-7127-430a-833e-72e462feb7b6)

Enter the names and color-codes and press the ok button.

### 3. Cell Detections

Open an image from the project and make an annotation of a region of interest (this can be the whole image, which you can select with ``ctrl+shift+a``). Open the ``Cell Detection`` dialog from the menu ``Analyze>Cell Detection``. We want to create a cell detector that works on the channel with the nuclei staining (for example DAPI). Adjust the parameters and try them on multiple images, until you get a satisfactory result. Note the parameters.

### 4. Object Classification

For each channel we will create a classifier. The classifier will map each cell to either the class of the channel meaning that the cell is positive for the staining in this channel or to the special class ``Ignore*``. Open an image and if not done yet, make an annotation and run the cell detection. Go to the ``Annotations Tab``. We first need to create a class for each channel. These classes could have the names of the corresponding channels. You can either create them manually using ``Add`` from the context menu of the class list or automatically using the command ``Populate from existing channels``. Note that the classes are attached to the project, not to the image, so they only have to be created once.

Train and save an object classifier for each staining of interest as described in the [qupath documentation](https://qupath.readthedocs.io/en/stable/docs/tutorials/multiplex_analysis.html#train-save-classifiers).

![image](https://github.com/user-attachments/assets/f7fa99d4-1af0-427c-851c-ec5a7948df2e)

### 5. Creation of a composite classifier

Open the dialog from the menu ``Classify>Object classification>Create composite classifier...``.

![Screenshot from 2025-01-07 16-50-41](https://github.com/user-attachments/assets/f59aa12b-5e24-4fd8-b570-35c089275f7a)

Select all the single channel classifier and create and save a composite classifier.

### 6. Multiplex classification

Run the script ``multiplex_classification.groovy`` via the ``Run for project`` command. 

![image](https://github.com/user-attachments/assets/3e6bcb07-d8d9-4945-88b5-1daf036c1d03)

In the dialog, select the composite classifier you created and saved before and fill in the other parameters, using the detection parameters you have noted before. Press the ``OK`` button. The script will run the detection and classifications on the whole project and save the results to ``.tsv``-files. Two output files will be written. The first contains the raw numbers for the classes as they are reported by QuPath. The second adds total numbers for each class using the ``convert_table.groovy`` script.

![image](https://github.com/user-attachments/assets/bf19a1e9-477b-4a76-92c1-e7f30e806d29)
