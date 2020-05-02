
CREATE TABLE class(
    class_id SERIAL PRIMARY KEY,
    course_num integer,
    course_name varchar(255),
    section_name varchar(255),
    term varchar(255),
    class_desc TEXT
);

CREATE TABLE category(
  cat_id SERIAL PRIMARY KEY,
  cat_name varchar(255) NOT NULL,
  cat_weight integer NOT NULL,
  class_id INTEGER NOT NULL REFERENCES class
);

CREATE TABLE item(
  item_id SERIAL PRIMARY KEY,
  item_name varchar(255) NOT NULL,
  item_points_worth integer NOT NULL,
  item_desc TEXT,
  cat_id INTEGER NOT NULL REFERENCES category
);

CREATE TABLE student(
  student_id SERIAL PRIMARY KEY,
  username varchar(255) NOT NULL UNIQUE,
  student_name varchar(255) NOT NULL
);

CREATE TABLE grade(
  grade_id SERIAL PRIMARY KEY,
  score integer NOT NULL,
  student_id INTEGER NOT NULL REFERENCES student,
  item_id INTEGER NOT NULL REFERENCES item
);

CREATE TABLE student_class(
  class_id INTEGER NOT NULL REFERENCES class,
  student_id INTEGER NOT NULL REFERENCES student,
  PRIMARY KEY (class_id, student_id)
);
