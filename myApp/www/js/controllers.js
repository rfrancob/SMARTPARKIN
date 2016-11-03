angular.module('starter.controllers', [])

.controller('appMain', function($scope, $ionicLoading, $timeout){
    //////////////////////////////Conexão//////////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    $scope.conectar = function conectarSIBConToken() {
      $scope.connect(       
        "CadastroUser2", 
        "KP_CadastroUser:Teste",
        "6b78a807c5324928be9f081b787cf0bb",
        function( arg ) {
          alert(arg);
      });      
    };
    $scope.connect = function( ontology, kp, token, retrollamadaIfOk ) {
      sofia2.joinToken( token, kp, function( mensajeSSAP ) {      
          if ( mensajeSSAP != null && mensajeSSAP.body.data != null && mensajeSSAP.body.ok == true ) {
            $scope.sessionKey = mensajeSSAP.sessionKey;          
            if (typeof( retrollamadaIfOk )==='undefined' ) {
            }else{
              retrollamadaIfOk( $scope.sessionKey );
            }
          }else{
              alert( "Error\n" +  mensajeSSAP.body.error );
          }
      });
    };////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
})

.controller('DashCtrl', function($scope, $ionicLoading, $timeout) {
  /////////////////////////Google Maps////////////////////////////////
  ////////////////////////////////////////////////////////////////////  
  $scope.vagas = [];
  $scope.valor = 0.0005;

  $scope.mapCreated = function(map) {
      $scope.contentString = '';
    
      $scope.infowindow = new google.maps.InfoWindow({
        content: $scope.contentString
      });
      
      var latlngPlace = new google.maps.LatLng(-16.673550, -49.269108);
          $scope.marker = new google.maps.Marker({
            map: map,
            title:"Vaga - 1",
            icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
            position: latlngPlace
        });      
      
      $scope.marker.addListener('click', function() {
        $scope.infowindow.open($scope.map, $scope.marker);
      });

      $scope.map = map;
  };
  $scope.centerOnMe = function () {
      console.log("Centering");
      if (!$scope.map) {
        return;
      }

      $scope.loading = $ionicLoading.show({
        content: 'Getting current location...',
        showBackdrop: false
      });

      navigator.geolocation.getCurrentPosition(function (pos) {
        console.log('Got pos', pos);
        $scope.map.setCenter(new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude));
        $scope.loading.hide();
      }, function (error) {
        alert('Unable to get location: ' + error.message);
      });
  };
  $scope.conectarSOFIA = function (token, instance) {    
    sofia2.joinToken(token, instance, function (mensajeSSAP) {
        if (mensajeSSAP !== null && mensajeSSAP.body.data !== null && mensajeSSAP.body.ok === true) {
          sessionKey = mensajeSSAP.sessionKey;
        } else {
          console.log("Error conectando del sib");
        }

        var callBack = function () {
          $scope.vagas = [];
          sofia2.queryWithQueryType('select * from Vagas ORDER BY contextData.timestamp DESC limit 1;', 'Vagas', 'SQLLIKE', null, function (message) {
            resultjson = [];
            for ( var i = 0; i < message.body.data.length; i++ ) {
                resultjson = JSON.stringify( message.body.data[ i ], undefined, 2 ) + "\n";   
                var obj = JSON.parse(resultjson);
                $scope.vagas.push(obj);
            }
          }, sessionKey);
        };

        callBack();
    });
  };
  $scope.updateOnME = function () {
    $scope.conectarSOFIA('6640dea04a3d4fe99b1d962ea11d77b0', 'KpVagaTesteKP:VagasDesc');
    console.log($scope.vagas)
    if (!$scope.map) {
        return;
    }
  $scope.loading = $ionicLoading.show({
      content: 'Atualizando informaçoes...',
      showBackdrop: false
  })
  if(typeof $scope.vagas[0] !== 'undefined'){
    $scope.contentString = '<div id="content">'+
      '<div id="siteNotice">'+
      '</div>'+
      '<h1 id="firstHeading" class="firstHeading">Vaga - 1</h1>'+
      '<div id="bodyContent">' + '<p><b>Tempo Estacionado:' + $scope.vagas[0].Vagas.estacionado + '</div>'+
    '<p><b>Custo Total: R$ ' + $scope.vagas[0].Vagas.estacionado*$scope.valor + '</div>'+
      '<p></b>'+'</div>';

  $scope.infowindow.setContent($scope.contentString);
    
    if($scope.vagas[0].Vagas.ocupada == 1){
      $scope.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/red-dot.png');
    }else{
      $scope.marker.setIcon('http://maps.google.com/mapfiles/ms/icons/green-dot.png');
    }
  }
  $scope.loading.hide();

};
  
  $scope.reload = function () {
    $scope.updateOnME()

    $timeout(function(){
        $scope.reload();
    },1000)
  };
    
  $scope.reload();
  ///////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////
})

.controller('ChatsCtrl', function($scope, Chats, $ionicLoading, $timeout, $window) {
  $scope.sessionKey = "";
  $scope.tasklist = "";
  $scope.tasklist2 = "";
  $scope.tasklist3 = "";
  $scope.tasklist4 = "";
  $scope.user = {
    nome: "",
    cpf: "",
    email: "",
    end: "",
    compl: "",
    bairro: "",
    cep: ""
  };  
  $scope.ontologyInstance = "";
  $scope.resultjson = [];
  $scope.result = [];
  $scope.transformar = function(){    
    $scope.tasklist = localStorage.getItem("tasklist");
    $scope.tasklist2 = localStorage.getItem("tasklist2");
    $scope.tasklist3 = localStorage.getItem("tasklist3");
    $scope.tasklist4 = localStorage.getItem("tasklist4");
  }
  $scope.reloadPage = function(){$window.location.reload();}

  /////////////////////////////Insert////////////////////////////////////
  $scope.salvar = function(){
    $scope.insert($scope.user.nome, $scope.user.cpf, $scope.user.email, $scope.user.end, $scope.user.compl, $scope.user.bairro, $scope.user.cep);
    localStorage.setItem("tasklist", $scope.user.nome);
    localStorage.setItem("tasklist2", $scope.user.cpf);
    localStorage.setItem("tasklist3", $scope.user.email);
    localStorage.setItem("tasklist4", $scope.user.cep); //+";"+$scope.user.cpf+";"+$scope.user.email+";"+$scope.user.cep);
    $scope.reloadPage();
  };
  $scope.insert = function enviarUsuario(nome, cpf, email, end, compl, bairro, cep){
    $scope.ontologyInstance='{"CadastroUser":{ "nome":"'+nome+'","cpf":"'+cpf+'","email":"'+email+'","end":"'+end+'","compl":"'+compl+'","bairro":"'+bairro+'","cep":"'+cep+'","key":[" "],"estacionado":" "}}';
    sofia2.insert($scope.ontologyInstance, 'CadastroUser2', function(mensajeSSAP){
      if(mensajeSSAP != null && mensajeSSAP.body.data != null && mensajeSSAP.body.ok == true){
        $(" ").text("Usuario Enviada").show();
      }else{
        $(" ").text("Error Enviando Usuario").show();
      }
    }, $scope.sessionKey);
    window.location = "http://localhost:8100/#/tab/dash";
  };
  /////////////////////////////////////////////////////////////////////// 
})

.controller('ChatDetailCtrl', function($scope, $stateParams, Chats) {
  $scope.chat = Chats.get($stateParams.chatId);
})

.controller('AccountCtrl', function($scope) {
  $scope.settings = {
    enableFriends: true
  };
  $scope.task = $scope.tasklist;
})

.controller('tabCtrl', function($scope){
  $scope.tasklist = localStorage.getItem('tasklist');
  $scope.mostrar = function(){
      if(localStorage.getItem('tasklist') == null){
        return true;
      }
  };
})