$ ->
  $.get "/recommendations?userid="+$("#userid_global").val(), (recommendations)->
    $.each recommendations, (movieid,estimatedrating)->
      $('#recs').append $("<li>").text recommendation.movieid+" - "+estimatedrating

  $('#update').click ->
    window.location.href="/userzone?userid="+$("#userid_global").val()
    ##HERE I update whole page