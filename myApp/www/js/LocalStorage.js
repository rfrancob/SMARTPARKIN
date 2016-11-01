function getTasks(){
  this.usuario = [];

  var lista = localStorage.getUser("user");

  if(lista !== null)
    this.usuario = angular.fromJson(lista);

    this.save = function () {
      localStorage.setItem("user", lista);
    }

    /*this.add = function(item){
      this.usuario.push(item);
    };

    this.remove = function (item){
      var pos = this.usuario.indexOf(item);
      this.usuario.splice(pos, 1);
    };*/

}