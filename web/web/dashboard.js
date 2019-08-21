//import { QuerySnapshot } from "@google-cloud/firestore";
var firebaseConfig = {
  apiKey: "AIzaSyB6U1Y6OH6rZKX9N1ovmibPps_Syi0ZJ9s",
  authDomain: "test-2a6ee.firebaseapp.com",
  databaseURL: "https://test-2a6ee.firebaseio.com",
  projectId: "test-2a6ee",
  storageBucket: "test-2a6ee.appspot.com",
  messagingSenderId: "125014748717",
  appId: "1:125014748717:web:2411be226b6536dd"
};
// Initialize Firebase
firebase.initializeApp(firebaseConfig);

// firebase.auth().onAuthStateChanged(function(user) {
//   if (user) {
//     // User is signed in.
//     $("#p2").show()

//     document.getElementById('Email').value=""
//     document.getElementById('Password').value=""
//     var dialog = document.querySelector('#login-dialog');
//     if (! dialog.showModal) {
//       dialogPolyfill.registerDialog(dialog);
//     }
//     dialog.close();
//     console.log(firebase.auth().currentUser.email);
//     $("#navemail").text(firebase.auth().currentUser.email);

//     var db=firebase.firestore();

//     db.collection("Profiles").get().then(function(querySnapshot) {
//     querySnapshot.forEach(function(doc) {
//         // doc.data() is never undefined for query doc snapshots
//         console.log(doc.id, " => ", doc.data());
//         var grid = document.getElementById("grid1");
//         console.log(doc.get("image"));
// var tab = ' <div class="mdl-cell content"><div class="page-content"><div class="demo-card-square mdl-card mdl-shadow--2dp" ><img src='+doc.get("image")+' style="width:100%;height:70%"><div class="mdl-card__supporting-text">Name: '+doc.get("Name")+'</div><div class="mdl-card__actions mdl-card--border"><a href="data.html?'+doc.id+'" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" id='+doc.id+'>View Profile</a></div></div></div></div>';
// grid.insertAdjacentHTML('afterbegin',tab);
//     });
//     $(".login-cover").hide();
//     $("#loginProgress").hide();
//     $("#login_button").show();
// });
   
//     // ...
//   } else {
//     // User is signed out.
//     console.log("abcd");
//     $(".login-cover").show();
//     $("#cover").show();
//     $("#p2").hide()
//     var dialog = document.querySelector('#login-dialog');
//     if (! dialog.showModal) {
//       dialogPolyfill.registerDialog(dialog);
//     }
//     dialog.showModal();
//     // ...
//   }
// });


var firestore = firebase.firestore();

// const docRef=firestore.doc("Leaders");

firestore.collection('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates').get().then((querySnapshot)=>{
    querySnapshot.forEach((doc)=>{
        var data = doc.data();
        console.log(data.Name);
        console.log(doc.id);
        $('#politician').append('<div class="col-lg-3 col-md-6 col-sm-6"><a onclick="myFunction(this)" name="'+doc.id+'" style="text-decoration:none;"><div class="card card-stats"><div class="card-body "><div class="row"><div class="col-12 col-md-4"><div class="icon-big text-center icon-warning"><img src="'+data.logo+'" class="party_icon" alt="bjp"></div></div><div class="col-0 col-md-8"><div class="numbers"><p class="card-title">'+data.party+'<p></div></div></div></div><div class="card-footer "><hr><div class="stats"><div class="row"><div class="col-12 col-md-4"><img class="circular_img" src="'+data.image+'" alt="img.."> </div><div clas="col-0 col-md-8"><h6>'+data.Name+'</h6><p>age:<strong>'+data.age+'yr </strong></p></div></div></div></div></div></a></div>');
    });
});

myFunction =function(element){
  console.log('inside '+element.name);
  window.open("detail.html?"+element.name); 
}

