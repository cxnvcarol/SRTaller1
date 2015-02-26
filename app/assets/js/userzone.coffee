$ ->
  $.get "/recommendations/"+$("#userid_global").val(), (recommendations)->
    $.each recommendations, (index,recommendation)->
      $('#recs').append $("<li>").text recommendation.movie.id+" - "+recommendation.movie.name+"   .............   "+recommendation.predictedRating

  $.get "/ratings/"+$("#userid_global").val(), (ratings)->
    $.each ratings, (index,rating)->
      $('#ratingsList').append $("<li>").text rating.movie.id+" - "+rating.movie.name+"   .............   "+rating.rating


  $.get "/movies", (movies)->
    $.each movies, (index,movie)->
      $("#allmovies").append $($("<option>").text movie.name).val movie.id

  $('#update').click ->
    window.location.href="/userzone?userid="+$("#userid_global").val()
    ##HERE I update whole page