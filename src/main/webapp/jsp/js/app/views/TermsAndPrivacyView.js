

var TermsAndPrivacyView = Backbone.View.extend({

   //DOM Event Handlers
   events: {
      'click [name~="privacy"]' : 'showPrivacy', // show privacy policy
      'click [name~="terms"]' : 'showTerms', //show terms of use
      'click [name~="closePrivacy"]' : 'closePrivacy', // hide privacy policy
      'click [name~="closeTerms"]' : 'closeTerms' //hide terms of use
   },

   //callback for show terms of use
   showTerms: function(){
      //show curtain
      $('#terms').fadeIn(1000);
      $('.curtain').fadeIn(1000);
      $('.sectionContact').css('overflow','hidden');
   },

   //callback for show privacy policy
   showPrivacy: function(){
       //show curtain
      $('#privacy').fadeIn(1000);
      $('.curtain').fadeIn(1000);
      $('.sectionContact').css('overflow','hidden');
   },
   
   //callback for closeTerms
   closeTerms: function(){
      //show curtain
      $('#terms').fadeOut(1000);
      $('.curtain').fadeOut(1000);
      $('.sectionContact').css('overflow','visible');
      $('html, body').animate({
        scrollTop: $("#termsAnchor").offset().top
      }, 0);
   },

   closePrivacy: function(){
      //show curtain
      $('#privacy').fadeOut(1000);
      $('.curtain').fadeOut(1000);
      $('.sectionContact').css('overflow','visible');
      $('html, body').animate({
        scrollTop: $("#privacyAnchor").offset().top
      }, 0);
   }
});
