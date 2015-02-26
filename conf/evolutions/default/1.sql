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
  rating                    double)
;

create table statistics_model (
  average_distance          double,
  max_distance              double,
  min_distance              double,
  standard_deviation        double,
  variance                  double,
  results_length            integer)
;

create table user (
  id                        integer primary key AUTOINCREMENT,
  is_new_user               integer(1))
;




# --- !Downs

PRAGMA foreign_keys = OFF;

drop table movie;

drop table rating;

drop table statistics_model;

drop table user;

PRAGMA foreign_keys = ON;

