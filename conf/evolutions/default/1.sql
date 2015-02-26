# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table movie (
  id                        bigint not null,
  average_rating            double,
  num_ratings               integer,
  name                      varchar(255),
  categories                varchar(255),
  constraint pk_movie primary key (id))
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
  id                        bigint not null,
  is_new_user               boolean,
  constraint pk_user primary key (id))
;

create sequence movie_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists movie;

drop table if exists rating;

drop table if exists statistics_model;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists movie_seq;

drop sequence if exists user_seq;

