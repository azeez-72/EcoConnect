<img align="left" height="50" width="50" src="https://github.com/azeez-72/EcoConnect/blob/main/images/EcoConnect_logo.png">

# EcoConnect - Google Solution Challenge 2021 #
## by Team **Coders.ktx** ##

### Team Members: ###

* Pankaj Khushalani - pankajkk218@gmail.com
* Azeez Dandawala - officialazeezamd@gmail.com
* Prasad Thakare - thakareprasad80@gmail.com

## Description: ##

### We aim to solve the targets: ###
1. Solid Waste Management under the **Goal 11 - Sustainable Cities.**
2. Improving recycling rates under the **Goal 12 - Responsible Consumption & Production.**

* EcoConnect connects individual households to NGOs near them that provide the means of garbage
  collection(recyclable waste) through scheduling pickups from households where they have been notified of and as well as processing the garbage for recycling or reuse. This is facilitated with
  the help of Google Maps. Along with the ability to connect and donate waste, users can
  detect whether the waste they have can be recycled or not. With the help of AutoML Vision
  Edge, a ML model is used to classify images of garbage. Thus, the user becomes aware of
  what can be donated and is given a means of whom/where to donate.
  
  #### **You can find the apk for our app [here](https://drive.google.com/file/d/14eqppFgWycH7AKjMBLRG0Xaj-4Eu6SYr/view?usp=sharing)** ####
  
## Tech Stack: ##
* We built an Android mobile application using the programming language Kotlin. MVVM (Model-View-ViewModel) architecture was followed throughout for ease of scaling and smooth communication with the UI.

* For the backend, Firebase was used extensively.
    * For authentication of individuals and organizations, Firebase Google Authentication was used.
    * For storing app data efficiently, Cloud Firestore was used.
    * For creating the image classification machine learning model, AutoML Vision Edge of Firebase and Google Cloud Platform were used. On GCP, Google Cloud Storage and Cloud Vision API were used. Using Cloud Vision API, a ML TFLite model was made for image classification using our custom images and labels.

* XML was used with Kotlin as the frontend. Navigation drawer was used and to make efficient use of XML, fragments were used extensively.
 
* Android Studio 4.2 Beta 6 was the IDE used to develop the Android mobile application. The app was thoroughly tested on the virtual device, Google Pixel 3a and other physical Android devices.

* Git and GitHub were used for version control and effective collaboration.
  
## Steps to install ##
  
Clone the app in your direcory using:

```bash
git clone https://github.com/azeez-72/EcoConnect.git
```
Once cloned, open Android Studio in that directory where you have cloned the project and run the app.
