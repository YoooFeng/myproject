

-- new Projects
CREATE TABLE `iPipeline`.`Projects` (
  `project_name` VARCHAR(50) NOT NULL,
  `github_url` VARCHAR(45) NULL,
  `user_name` VARCHAR(45) NULL,
  PRIMARY KEY (`project_name`),
  UNIQUE INDEX `project_name_UNIQUE` (`project_name` ASC));



-- new Users
CREATE TABLE `iPipeline`.`Users` (
  `user_id` INT NOT NULL,
  `user_name` VARCHAR(45) NOT NULL,
  `user_password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_name_UNIQUE` (`user_name` ASC));

-- Data init
insert into Users values("yf", "111111");

insert into Projects values("taleDemo", "https://github.com/YoooFeng/tale.git", "yf");
insert into Projects values("JHipsterDemo", "https://github.com/YoooFeng/JHipsterDemo.git", "yf");


