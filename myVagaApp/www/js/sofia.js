/*

var sessionKey;
var ocupada = [];
var vagaid = [];

function conectarSIB(token, instance) {
  sofia2.joinToken(token, instance, function (mensajeSSAP) {

    if (mensajeSSAP !== null && mensajeSSAP.body.data !== null && mensajeSSAP.body.ok === true) {
      sessionKey = mensajeSSAP.sessionKey;
    } else {
      console.log("Error conectando del sib");
    }


    var callBack = function () {

      sofia2.queryWithQueryType('select * from KpVagaTeste WHERE KpVagaTeste.ocupada = 1 ORDER BY KpVagaTeste.contextData DESC limit 2;', 'KpVagaTeste', 'SQLLIKE', null, function (message) {
        ocupada[1] = message.body.data[0].KpVagaTeste.ocupada;
        vagaid[1] = message.body.data[0].KpVagaTeste.vagaid;
        callBack();

      }, sessionKey);
    };

  callBack();
  });
}

window.onload = function () {
	conectarSIB('6640dea04a3d4fe99b1d962ea11d77b0', 'KpVagaTesteKP:VagasDesc');
}
*/
