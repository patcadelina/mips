(function($){
   $(document).ready(function(){
      new MainRouter(); // declare a router
      Backbone.history.start(); // start url tracking
   });
})(jQuery);
