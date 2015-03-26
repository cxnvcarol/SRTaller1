# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table movie (
  id                        integer primary key AUTOINCREMENT,
  average_rating            double,
  num_ratings               integer,
  name                      varchar(255),
  categories                varchar(255))
;

create table rating (
  rating                    double,
  timestamp                 integer)
;

create table recommendation (
  predicted_rating          double)
;

create table result_model (
  distance                  double,
  estimated_rating          double,
  real_rating               double,
  item_id                   integer,
  user_id                   integer)
;

create table statistics_model (
  average_distance          double,
  max_distance              double,
  min_distance              double,
  standard_deviation        double,
  variance                  double,
  results_length            integer,
  description               varchar(255))
;

create table user (
  id                        integer primary key AUTOINCREMENT,
  ratings_model             integer(1),
  is_new_user               integer(1))
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table movie;

drop table rating;

drop table recommendation;

drop table result_model;

drop table statistics_model;

drop table user;

PRAGMA foreign_keys = ON;

