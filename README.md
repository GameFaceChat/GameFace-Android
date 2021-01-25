# GameFace
<img src="https://circleci.com/gh/GameFaceChat/GameFace-Android.svg?style=svg" />
<p align="center">
  <img src="https://user-images.githubusercontent.com/37857112/105650883-3225d900-5e83-11eb-9ae0-246b7a654b4a.png" width="400" height="400">
</p>

GameFace is a group video call application in which users can also play games with each other while on call. Users can purchase "game packs" from the store, and then use these packs within the video call application. Each user's profile shows the amount of games that the user has won, and the number of packs they have purchased. Every time you win a game, you gain game points which can be used to purchase packs.

This project is in the process of being open-sourced, so please bear with me while I work on the documentation. If you have any questions, please feel free to email me at srihari.vishnu@gmail.com

## Technical Features
Used the WebRTC framework to build a real-time group video chat application. The connections are in Peer to Peer mesh topology, that is, every user is connected to ever other user. This eliminates the need for a central server. 

- Uses Firebase for storing users and store data
- Cloud functions REST API to retrieve STUN and TURN servers from Twilio and purchase/download packs from a shop
- Android Jetpack Navigation Features

## Screenshots

<p float="left">
    <img width="250" alt="Screen Shot 2021-01-23 at 8 09 06 PM" src="https://user-images.githubusercontent.com/37857112/105618757-e4449e80-5db8-11eb-974c-a842fbbb3e73.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 09 17 PM" src="https://user-images.githubusercontent.com/37857112/105618758-e4449e80-5db8-11eb-84cd-98338495ecde.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 00 15 PM" src="https://user-images.githubusercontent.com/37857112/105618751-e1e24480-5db8-11eb-96cc-98a5b4dd9a35.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 00 33 PM" src="https://user-images.githubusercontent.com/37857112/105618752-e27adb00-5db8-11eb-86eb-e7884b960b75.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 00 55 PM" src="https://user-images.githubusercontent.com/37857112/105618753-e27adb00-5db8-11eb-9bf5-b3a3b4a5a616.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 01 24 PM" src="https://user-images.githubusercontent.com/37857112/105618754-e3137180-5db8-11eb-93a3-3855cd79d4bd.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 01 39 PM" src="https://user-images.githubusercontent.com/37857112/105618755-e3ac0800-5db8-11eb-9ee1-cb8eebd7c4af.png">
  <img width="250" alt="Screen Shot 2021-01-23 at 8 08 08 PM" src="https://user-images.githubusercontent.com/37857112/105618756-e3ac0800-5db8-11eb-9364-f1505e897701.png">

</p>









