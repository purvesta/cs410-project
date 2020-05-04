--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: category; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.category (
    cat_id integer NOT NULL,
    cat_name character varying(255) NOT NULL,
    cat_weight integer NOT NULL,
    class_id integer NOT NULL
);


ALTER TABLE public.category OWNER TO tpurves;

--
-- Name: category_cat_id_seq; Type: SEQUENCE; Schema: public; Owner: tpurves
--

CREATE SEQUENCE public.category_cat_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_cat_id_seq OWNER TO tpurves;

--
-- Name: category_cat_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tpurves
--

ALTER SEQUENCE public.category_cat_id_seq OWNED BY public.category.cat_id;


--
-- Name: class; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.class (
    class_id integer NOT NULL,
    course_num character varying(255),
    section_num integer,
    term character varying(255),
    class_desc text
);


ALTER TABLE public.class OWNER TO tpurves;

--
-- Name: class_class_id_seq; Type: SEQUENCE; Schema: public; Owner: tpurves
--

CREATE SEQUENCE public.class_class_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.class_class_id_seq OWNER TO tpurves;

--
-- Name: class_class_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tpurves
--

ALTER SEQUENCE public.class_class_id_seq OWNED BY public.class.class_id;


--
-- Name: grade; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.grade (
    grade_id integer NOT NULL,
    score integer NOT NULL,
    student_id integer NOT NULL,
    item_id integer NOT NULL
);


ALTER TABLE public.grade OWNER TO tpurves;

--
-- Name: grade_grade_id_seq; Type: SEQUENCE; Schema: public; Owner: tpurves
--

CREATE SEQUENCE public.grade_grade_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.grade_grade_id_seq OWNER TO tpurves;

--
-- Name: grade_grade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tpurves
--

ALTER SEQUENCE public.grade_grade_id_seq OWNED BY public.grade.grade_id;


--
-- Name: item; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.item (
    item_id integer NOT NULL,
    item_name character varying(255) NOT NULL,
    item_points_worth integer NOT NULL,
    item_desc text,
    cat_id integer NOT NULL
);


ALTER TABLE public.item OWNER TO tpurves;

--
-- Name: item_item_id_seq; Type: SEQUENCE; Schema: public; Owner: tpurves
--

CREATE SEQUENCE public.item_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.item_item_id_seq OWNER TO tpurves;

--
-- Name: item_item_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tpurves
--

ALTER SEQUENCE public.item_item_id_seq OWNED BY public.item.item_id;


--
-- Name: student; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.student (
    student_id integer NOT NULL,
    username character varying(255) NOT NULL,
    student_name character varying(255) NOT NULL
);


ALTER TABLE public.student OWNER TO tpurves;

--
-- Name: student_class; Type: TABLE; Schema: public; Owner: tpurves
--

CREATE TABLE public.student_class (
    class_id integer NOT NULL,
    student_id integer NOT NULL
);


ALTER TABLE public.student_class OWNER TO tpurves;

--
-- Name: student_student_id_seq; Type: SEQUENCE; Schema: public; Owner: tpurves
--

CREATE SEQUENCE public.student_student_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.student_student_id_seq OWNER TO tpurves;

--
-- Name: student_student_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tpurves
--

ALTER SEQUENCE public.student_student_id_seq OWNED BY public.student.student_id;


--
-- Name: category cat_id; Type: DEFAULT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.category ALTER COLUMN cat_id SET DEFAULT nextval('public.category_cat_id_seq'::regclass);


--
-- Name: class class_id; Type: DEFAULT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.class ALTER COLUMN class_id SET DEFAULT nextval('public.class_class_id_seq'::regclass);


--
-- Name: grade grade_id; Type: DEFAULT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.grade ALTER COLUMN grade_id SET DEFAULT nextval('public.grade_grade_id_seq'::regclass);


--
-- Name: item item_id; Type: DEFAULT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.item ALTER COLUMN item_id SET DEFAULT nextval('public.item_item_id_seq'::regclass);


--
-- Name: student student_id; Type: DEFAULT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student ALTER COLUMN student_id SET DEFAULT nextval('public.student_student_id_seq'::regclass);


--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.category (cat_id, cat_name, cat_weight, class_id) FROM stdin;
5	Tests	40	8
6	Quizzes	20	8
7	Participation	10	8
8	Projects	30	8
\.


--
-- Data for Name: class; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.class (class_id, course_num, section_num, term, class_desc) FROM stdin;
8	CS410	1	Fa19	Databases.
9	CS410	2	Fa19	Databases.
10	CS455	1	Fa19	Distributed Systems.
\.


--
-- Data for Name: grade; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.grade (grade_id, score, student_id, item_id) FROM stdin;
4	80	1	7
5	9	1	9
6	7	1	10
7	100	1	12
8	90	1	13
9	90	2	7
\.


--
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.item (item_id, item_name, item_points_worth, item_desc, cat_id) FROM stdin;
7	Test 1	100	Rather easy test.	5
8	Test 2	100	More difficult test.	5
9	Quiz 1	10	Weekly quiz.	6
10	Quiz 2	10	Weekly quiz.	6
11	Quiz 3	10	Weekly quiz.	6
12	Participation	100	Semester long participation.	7
13	Project 1	100	A simple DB application.	8
14	Project 2	100	A more difficult DB application.	8
\.


--
-- Data for Name: student; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.student (student_id, username, student_name) FROM stdin;
1	purvesta	Purves, Tanner
2	zestyfe	Pace, Preston
3	durkinza	Durkin, Zane
\.


--
-- Data for Name: student_class; Type: TABLE DATA; Schema: public; Owner: tpurves
--

COPY public.student_class (class_id, student_id) FROM stdin;
8	1
8	2
8	3
\.


--
-- Name: category_cat_id_seq; Type: SEQUENCE SET; Schema: public; Owner: tpurves
--

SELECT pg_catalog.setval('public.category_cat_id_seq', 8, true);


--
-- Name: class_class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: tpurves
--

SELECT pg_catalog.setval('public.class_class_id_seq', 10, true);


--
-- Name: grade_grade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: tpurves
--

SELECT pg_catalog.setval('public.grade_grade_id_seq', 9, true);


--
-- Name: item_item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: tpurves
--

SELECT pg_catalog.setval('public.item_item_id_seq', 14, true);


--
-- Name: student_student_id_seq; Type: SEQUENCE SET; Schema: public; Owner: tpurves
--

SELECT pg_catalog.setval('public.student_student_id_seq', 1, false);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (cat_id);


--
-- Name: class class_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_id);


--
-- Name: grade grade_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_pkey PRIMARY KEY (grade_id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (item_id);


--
-- Name: student_class student_class_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student_class
    ADD CONSTRAINT student_class_pkey PRIMARY KEY (class_id, student_id);


--
-- Name: student student_pkey; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (student_id);


--
-- Name: student student_username_key; Type: CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student
    ADD CONSTRAINT student_username_key UNIQUE (username);


--
-- Name: category category_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: grade grade_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_item_id_fkey FOREIGN KEY (item_id) REFERENCES public.item(item_id);


--
-- Name: grade grade_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.grade
    ADD CONSTRAINT grade_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.student(student_id);


--
-- Name: item item_cat_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_cat_id_fkey FOREIGN KEY (cat_id) REFERENCES public.category(cat_id);


--
-- Name: student_class student_class_class_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student_class
    ADD CONSTRAINT student_class_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.class(class_id);


--
-- Name: student_class student_class_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tpurves
--

ALTER TABLE ONLY public.student_class
    ADD CONSTRAINT student_class_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.student(student_id);


--
-- PostgreSQL database dump complete
--

