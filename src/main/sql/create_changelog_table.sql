drop table if exists CHANGELOG;

create table CHANGELOG (
  CHANGE_NUMBER bigint not null,
  COMPLETE_DT timestamp not null,
  APPLIED_BY varchar(100) not null,
  DESCRIPTION varchar(500) not null
);

alter table CHANGELOG add constraint PKCHANGELOG primary key (CHANGE_NUMBER);
alter table CHANGELOG engine InnoDB;