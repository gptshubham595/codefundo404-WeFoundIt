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


var firestore = firebase.firestore();
var partyName = document.getElementById('partyName');
var partyImg = document.getElementById('partyImg');
var no=0;

// const docRef=firestore.doc("Leaders");


var url = document.location.href ;
var params= url.split('?')[1];


var sp = document.getElementById('sp');
sp.style.display = "block";
// var ref = fire/store.doc('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params);
firestore.doc('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params).get().then((doc)=>{
    if(doc && doc.exists){
        var data=doc.data();
        document.getElementById('politicianImg').src=data.image;
        partyImg.src=data.logo;
        partyName.innerText = data.party;
        no = data.noOfComment;
        console.log(no);
    }
})

firestore.collection('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params+'/Comments').get().then((querySnapshot)=>{
    querySnapshot.forEach((doc)=>{
        var data = doc.data();
        $('.comments').append('<div class="comment"><a class="avatar"><img src="Profile-ICon.png"></a><div class="content" ><a class="author">Annonymous</a><div class="metadata"><span class="date" id="'+doc.id+'L" style="color:green;">'+data.Likes+' Likes</span><div class="date"  id="'+doc.id+'D" style="color:red;">'+data.Dislikes+' Dislikes</div></div><div class="text"><p>'+data.Value+'</p></div><div class="actions"><a class="reply" id="like" onclick="clickedLike(this)" name="'+doc.id+'">Like</a><a class="reply" id="like" onclick="clickedDislike(this)" name="'+doc.id+'">Dislike</a></div></div></div>');
        console.log(data.Value);
    });
    sp.style.display = "none";
});

myfunction =function(){
    
}

clickedLike = function(element){
    firestore.doc('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params+'/Comments/'+element.name).update({
        Likes:firebase.firestore.FieldValue.increment(1)
    }).then(()=>{
        var data = $('#'+element.name+'L').html();
        console.log()
        document.getElementById(element.name+"L").innerText = String(parseInt(data)+1)+" Likes";
        console.log(data);
    });
}
clickedDislike = function(element){
    firestore.doc('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params+'/Comments/'+element.name).update({
        Dislikes:firebase.firestore.FieldValue.increment(1)
    }).then(()=>{
        var data = $('#'+element.name+'D').html();
        console.log()
        document.getElementById(element.name+"D").innerText = String(parseInt(data)+1)+" Dislikes";
        console.log(data);
    });
}

addComment = function(){
    var data = document.getElementById('commentText').value;
    console.log(data);
    firestore.collection('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params+'/Comments/').doc(String(no+1)).set(
        {
            Likes:0,
            Dislikes:0,
            Value:data
        }
    ).then(()=>{
        document.getElementById('commentText').value="";
        document.getElementById('commm').innerHTML = "";
        firestore.doc('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params).update({
            noOfComment:firebase.firestore.FieldValue.increment(1)
        }).then(()=>{
            firestore.collection('Constituencies/iZ3vWPVuYJiTssYdwmEZ/Candidates/'+params+'/Comments').get().then((querySnapshot)=>{
                querySnapshot.forEach((doc)=>{
                    var data = doc.data();
                    $('.comments').append('<div class="comment"><a class="avatar"><img src="Profile-ICon.png"></a><div class="content" ><a class="author">Annonymous</a><div class="metadata"><span class="date" id="'+doc.id+'L" style="color:green;">'+data.Likes+' Likes</span><div class="date"  id="'+doc.id+'D" style="color:red;">'+data.Dislikes+' Dislikes</div></div><div class="text"><p>'+data.Value+'</p></div><div class="actions"><a class="reply" id="like" onclick="clickedLike(this)" name="'+doc.id+'">Like</a><a class="reply" id="like" onclick="clickedDislike(this)" name="'+doc.id+'">Dislike</a></div></div></div>');
                    console.log(data.Value);
                });
            });
        });
        // $('.comments').append('<div class="comment"><a class="avatar"><img src="Profile-ICon.png"></a><div class="content" ><a class="author">Annonymous</a><div class="metadata"><span class="date" id="useridL">0 Likes</span><div class="date"  id="useridD">0 Dislikes</div></div><div class="text"><p>The hours, minutes and seconds stand as visible reminders that your effort put them all there. </p><p>Preserve until your next run, when the watch lets you see how Impermanent your efforts are.</p></div><div class="actions"><a class="reply" id="like" onclick="clickedLike(this)" name="userid">Like</a><a class="reply" id="like" onclick="clickedDislike(this)" name="userid">Dislike</a></div></div></div>');
    });
}