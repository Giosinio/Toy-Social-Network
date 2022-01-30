--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

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
-- Name: event_participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_participants (
    event_id integer NOT NULL,
    user_id character varying NOT NULL,
    enrollment_date timestamp without time zone NOT NULL,
    subscribed boolean DEFAULT true NOT NULL,
    subscription_date date NOT NULL
);


ALTER TABLE public.event_participants OWNER TO postgres;

--
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events (
    id integer NOT NULL,
    event_name character varying NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    organiser character varying NOT NULL,
    category character varying NOT NULL,
    description character varying NOT NULL,
    location character varying NOT NULL
);


ALTER TABLE public.events OWNER TO postgres;

--
-- Name: events_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.events_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.events_id_seq OWNER TO postgres;

--
-- Name: events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.events_id_seq OWNED BY public.events.id;


--
-- Name: friendship_requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friendship_requests (
    from_username character varying NOT NULL,
    to_username character varying NOT NULL,
    created date NOT NULL
);


ALTER TABLE public.friendship_requests OWNER TO postgres;

--
-- Name: friendships; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friendships (
    user1 character varying NOT NULL,
    user2 character varying NOT NULL,
    date date NOT NULL
);


ALTER TABLE public.friendships OWNER TO postgres;

--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id integer NOT NULL,
    from_user character varying NOT NULL,
    message character varying NOT NULL,
    replies_to integer,
    sent_date timestamp without time zone NOT NULL,
    subject character varying(50) NOT NULL,
    "group" integer NOT NULL
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: messages_group_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messages_group_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_group_seq OWNER TO postgres;

--
-- Name: messages_group_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messages_group_seq OWNED BY public.messages."group";


--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO postgres;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messages_id_seq OWNED BY public.messages.id;


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.notifications (
    id integer NOT NULL,
    event_id integer NOT NULL,
    user_id character varying NOT NULL,
    display_date date NOT NULL,
    disabled boolean DEFAULT false NOT NULL
);


ALTER TABLE public.notifications OWNER TO postgres;

--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.notifications_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO postgres;

--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: recipients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.recipients (
    user_id character varying NOT NULL,
    message_id integer NOT NULL
);


ALTER TABLE public.recipients OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    username character varying NOT NULL,
    password character varying NOT NULL,
    firstname character varying NOT NULL,
    lastname character varying NOT NULL,
    salt character varying NOT NULL,
    lastloginat date
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: events id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events ALTER COLUMN id SET DEFAULT nextval('public.events_id_seq'::regclass);


--
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages ALTER COLUMN id SET DEFAULT nextval('public.messages_id_seq'::regclass);


--
-- Name: messages group; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages ALTER COLUMN "group" SET DEFAULT nextval('public.messages_group_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Data for Name: event_participants; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_participants (event_id, user_id, enrollment_date, subscribed, subscription_date) FROM stdin;
16	george.giosan@gmail.com	2022-01-15 03:16:40.577535	t	2022-01-15
20	george.giosan@gmail.com	2022-01-15 03:16:42.283537	t	2022-01-15
18	george.giosan@gmail.com	2022-01-15 03:17:55.26206	t	2022-01-15
14	george.giosan@gmail.com	2022-01-15 03:16:38.31643	f	2022-01-15
17	george.giosan@gmail.com	2022-01-15 03:17:56.578608	f	2022-01-15
13	george.giosan@gmail.com	2022-01-15 03:16:41.423387	f	2022-01-15
17	stefan.farcasanu@gmail.com	2022-01-15 03:19:30.478691	t	2022-01-15
14	stefan.farcasanu@gmail.com	2022-01-15 03:19:30.772813	t	2022-01-15
18	stefan.farcasanu@gmail.com	2022-01-15 03:19:30.962663	t	2022-01-15
19	stefan.farcasanu@gmail.com	2022-01-15 03:19:31.099211	t	2022-01-15
20	stefan.farcasanu@gmail.com	2022-01-15 03:19:31.23224	t	2022-01-15
13	stefan.farcasanu@gmail.com	2022-01-15 03:19:31.36015	t	2022-01-15
15	stefan.farcasanu@gmail.com	2022-01-15 03:19:31.489005	t	2022-01-15
16	stefan.farcasanu@gmail.com	2022-01-15 03:19:31.617106	t	2022-01-15
20	victor.doroftei@gmail.com	2022-01-15 03:19:48.520698	t	2022-01-15
14	victor.doroftei@gmail.com	2022-01-15 03:19:49.086774	t	2022-01-15
16	victor.doroftei@gmail.com	2022-01-15 03:19:49.761929	t	2022-01-15
15	victor.doroftei@gmail.com	2022-01-15 03:19:50.582625	t	2022-01-15
17	victor.gradinariu@gmail.com	2022-01-15 03:20:11.601548	t	2022-01-15
14	victor.gradinariu@gmail.com	2022-01-15 03:20:11.819093	t	2022-01-15
18	victor.gradinariu@gmail.com	2022-01-15 03:20:12.070591	t	2022-01-15
19	victor.gradinariu@gmail.com	2022-01-15 03:20:12.420202	t	2022-01-15
17	iulia.filimon@gmail.com	2022-01-15 03:21:23.544919	t	2022-01-15
14	iulia.filimon@gmail.com	2022-01-15 03:21:23.705701	t	2022-01-15
18	iulia.filimon@gmail.com	2022-01-15 03:21:23.830369	t	2022-01-15
19	iulia.filimon@gmail.com	2022-01-15 03:21:23.963294	t	2022-01-15
15	iulia.filimon@gmail.com	2022-01-15 03:21:24.970261	t	2022-01-15
16	iulia.filimon@gmail.com	2022-01-15 03:21:25.178055	t	2022-01-15
17	laura.deac@gmail.com	2022-01-15 03:21:50.664116	t	2022-01-15
19	laura.deac@gmail.com	2022-01-15 03:21:51.584114	t	2022-01-15
18	laura.deac@gmail.com	2022-01-15 03:21:52.568452	t	2022-01-15
13	laura.deac@gmail.com	2022-01-15 03:21:53.217941	t	2022-01-15
15	laura.deac@gmail.com	2022-01-15 03:21:53.442183	t	2022-01-15
14	laura.deac@gmail.com	2022-01-15 03:21:54.245672	t	2022-01-15
17	mihai.dretcanu@gmail.com	2022-01-15 03:22:10.720442	t	2022-01-15
19	mihai.dretcanu@gmail.com	2022-01-15 03:22:11.601657	t	2022-01-15
15	mihai.dretcanu@gmail.com	2022-01-15 03:22:13.455379	t	2022-01-15
16	mihai.dretcanu@gmail.com	2022-01-15 03:22:13.627399	t	2022-01-15
17	catalin.halbes@gmail.com	2022-01-15 03:22:28.344214	t	2022-01-15
19	catalin.halbes@gmail.com	2022-01-15 03:22:29.193518	t	2022-01-15
20	catalin.halbes@gmail.com	2022-01-15 03:22:29.912632	t	2022-01-15
18	catalin.halbes@gmail.com	2022-01-15 03:22:30.643234	t	2022-01-15
17	ionela.gagea@gmail.com	2022-01-15 03:22:44.293934	t	2022-01-15
14	ionela.gagea@gmail.com	2022-01-15 03:22:44.430387	t	2022-01-15
18	ionela.gagea@gmail.com	2022-01-15 03:22:44.576211	t	2022-01-15
15	ionela.gagea@gmail.com	2022-01-15 03:22:46.395724	t	2022-01-15
20	ionela.gagea@gmail.com	2022-01-15 03:22:47.578931	t	2022-01-15
17	maria.ghita@gmail.com	2022-01-15 03:23:00.048078	t	2022-01-15
14	maria.ghita@gmail.com	2022-01-15 03:23:00.199416	t	2022-01-15
18	maria.ghita@gmail.com	2022-01-15 03:23:00.350756	t	2022-01-15
17	andrei.back@gmail.com	2022-01-15 03:23:12.811165	t	2022-01-15
14	andrei.back@gmail.com	2022-01-15 03:23:13.112412	t	2022-01-15
18	andrei.back@gmail.com	2022-01-15 03:23:13.239545	t	2022-01-15
19	andrei.back@gmail.com	2022-01-15 03:23:13.366727	t	2022-01-15
20	andrei.back@gmail.com	2022-01-15 03:23:13.4814	t	2022-01-15
13	andrei.back@gmail.com	2022-01-15 03:23:13.827912	t	2022-01-15
15	andrei.back@gmail.com	2022-01-15 03:23:13.962841	t	2022-01-15
16	andrei.back@gmail.com	2022-01-15 03:23:14.096282	t	2022-01-15
\.


--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.events (id, event_name, start_date, end_date, organiser, category, description, location) FROM stdin;
13	Steaua-Petrolul	2022-01-28	2022-01-28	FC Steaua Bucuresti	Sports	Meci de fotbal din Liga Nationala de fotbal, intre FCSB si Petrolul Ploiesti	Arena Nationala, Bucuresti
14	Maraton de desen	2022-01-18	2022-01-19	Muzeul Arta Lemnului	Culture	Maraton de desen desfasurat pe o perioada de 24h. Va asteptam cu drag!	Campulung Moldovenesc
15	Jazz in the Park	2022-08-27	2022-08-31	Primaria Cluj-Napoca	Culture	Va invitam la evenimentul Jazz in the Park, locul unde se aduna toti iubitorii jazz-ului.	Parcul Central
16	Electric Castle	2022-08-27	2022-08-31	EC Team	Concert	Electric Castle 9	Banffy Castle
17	Content Creators 2022	2022-01-16	2022-01-19	Revista Biz	Entertainment	Afla cum gandeste un creator de continut. Unii dintre cei mai cunoscuti creatori de continut vor fi prezenti, nu rata aceasta unica ocazie.	Online
18	HoReCa Digitala	2022-01-18	2022-01-18	pe-plus.ro	Webinar	Seria de evenimente online HoReCa Digitala isi propune sa identifice si sa analizeze punctele de diferentiere majora in modelul de business din sectorul de food-service, de dinainte si dupa pandemie.	Online
19	Atelier de Creatie	2022-01-18	2022-01-19	Atelierul de Istorie	Webinar	Eliberare emotionala, setare intentii si manifestare obiective pentru anul 2022	Online
20	Club de Carte 22 ianuarie	2022-01-22	2022-01-22	Clubul de Carte S&F	Culture	Urmatoarea intalnire din cadrul clubului de carte va avea loc in data de 22 ianuarie.	Strada Bucuresti 66 Cluj-Napoca
\.


--
-- Data for Name: friendship_requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friendship_requests (from_username, to_username, created) FROM stdin;
ionela.gagea@gmail.com	george.giosan@gmail.com	2022-01-15
maria.ghita@gmail.com	george.giosan@gmail.com	2022-01-15
andrei.back@gmail.com	george.giosan@gmail.com	2022-01-15
victor.gradinariu@gmail.com	george.giosan@gmail.com	2022-01-15
\.


--
-- Data for Name: friendships; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friendships (user1, user2, date) FROM stdin;
george.giosan@gmail.com	stefan.farcasanu@gmail.com	2022-01-15
george.giosan@gmail.com	livius.habuc@gmail.com	2022-01-15
catalin.halbes@gmail.com	george.giosan@gmail.com	2022-01-15
george.giosan@gmail.com	iulia.filimon@gmail.com	2022-01-15
george.giosan@gmail.com	mihai.dretcanu@gmail.com	2022-01-15
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, from_user, message, replies_to, sent_date, subject, "group") FROM stdin;
111	george.giosan@gmail.com	Salut, oare ai putea sa imi platesti tu caminul luna asta? Iti trimit pe BT banii	\N	2022-01-15 01:48:39.342696	Camin	11
112	andrei.back@gmail.com	Da, iti platesc, nu ii stres :)	111	2022-01-15 01:49:48.09481	Reply:Camin	12
113	george.giosan@gmail.com	Mersi mult, raman dator! :D	112	2022-01-15 01:50:35.251373	Reply: Camin	13
114	andrei.back@gmail.com	Stai linistit	113	2022-01-15 01:51:07.929377	Reply: Camin	14
115	stefan.farcasanu@gmail.com	Heei, ne strangem diseara la un CATAN?	\N	2022-01-15 01:52:53.347154	Catan	15
116	mihai.dretcanu@gmail.com	Da, eu ma bag, pe la ce ora?	115	2022-01-15 01:54:07.502703	Reply: Catan	16
117	catalin.halbes@gmail.com	Si eu cred ca vin, dar posibil sa intarzii putin	115	2022-01-15 01:57:39.118908	Reply: CATAN	17
118	george.giosan@gmail.com	Eu nu cred ca pot ajunge, imi pare scuze :(	115	2022-01-15 01:59:14.859818	Reply: CATAN	18
119	stefan.farcasanu@gmail.com	Hai maaa, ce ai mai bun de facut?	118	2022-01-15 02:00:34.663697	Reply: Motiv	19
120	george.giosan@gmail.com	Ies diseara cu colegii de camin, scuze. Lasam pe alta data	119	2022-01-15 02:01:28.468017	Reply: Motiv	20
121	stefan.farcasanu@gmail.com	Mna bine, distractie sa aveti :D	120	2022-01-15 02:01:56.459031	Reply: Greeting	21
122	george.giosan@gmail.com	Mersi, la feeeel, ne auzim 	121	2022-01-15 02:02:58.898639	Reply	22
123	george.giosan@gmail.com	Hei, ne vedem diseara la un TBOI pe discord?	\N	2022-01-15 02:04:19.704717	TBOI	23
124	catalin.halbes@gmail.com	Da, hai pe la un 10, mai facem ceva challenge-uri	123	2022-01-15 02:06:03.555064	Reply: TBOI	24
126	george.giosan@gmail.com	Bun, il chem si pe Andrei daca vrea sa vina?	124	2022-01-15 02:17:17.896408	Reply: TBOI	26
127	catalin.halbes@gmail.com	Da, de ce nu?	126	2022-01-15 02:18:17.934118	Reply: TBOI	27
128	george.giosan@gmail.com	Salut, ne vedem azi sa lucram la MAP?	\N	2022-01-15 02:21:33.139396	MAP	28
129	andrei.grigorescu@gmail.com	Da, pe la cat ai vrea sa ne vedem?	128	2022-01-15 02:21:51.240428	Reply: MAP	29
130	george.giosan@gmail.com	Pe la 8 ti-e ok? Ma gandeam sa ne uitam putin peste partea de mesaje la modul cum ne construim conversatia	129	2022-01-15 02:23:44.966482	Reply: MAP	30
131	andrei.grigorescu@gmail.com	Da, ne auzim atunci. Mai am si eu nevoie de niste sfaturi pe partea de GUI.	130	2022-01-15 02:24:23.592252	Reply: MAP	31
132	george.giosan@gmail.com	Heei, imi trimit te rog inregistrarile cu cursurile de la BD si PS de azi?	\N	2022-01-15 02:25:28.740903	Inregistrari Curs	32
133	mara.gheorghe@gmail.com	Desigur, ti le trimit imediat	132	2022-01-15 02:26:17.338177	Reply: Cursuri	33
134	george.giosan@gmail.com	Mersi mult de tot :)	133	2022-01-15 02:26:46.499241	Reply: Cursuri	34
135	mara.gheorghe@gmail.com	N-ai de ce :D	134	2022-01-15 02:27:04.437081	Reply: Cursuri	35
136	george.giosan@gmail.com	Ceau Victore, iesim diseara la bere?	\N	2022-01-15 02:27:48.90059	Bere	36
137	victor.gradinariu@gmail.com	As iesi? Cine mai vine?	136	2022-01-15 02:28:45.35729	Reply: Bere	37
138	george.giosan@gmail.com	Am vorbit cu Farca si Victor, o zis ca vin si ei. Poti sa mai intrebi si tu lume	137	2022-01-15 02:29:35.15137	Reply: Bere	38
139	victor.gradinariu@gmail.com	Na bine. Iesim in Piata Muzeului?	138	2022-01-15 02:30:01.138587	Reply: Bere	39
140	george.giosan@gmail.com	Dap	139	2022-01-15 02:31:37.17827	Reply: Bere	40
141	george.giosan@gmail.com	Mitzuleeeee	\N	2022-01-15 02:32:42.113925	Subiect	41
142	mihai.dretcanu@gmail.com	Giorgiooooo	141	2022-01-15 02:33:18.034105	Reply Giorgio	42
143	george.giosan@gmail.com	Mitzule, te bagi la un karaoke diseara?	142	2022-01-15 02:34:56.638645	Karaoke	43
144	mihai.dretcanu@gmail.com	Desigur	143	2022-01-15 02:35:59.951463	Reply: Karaoke	44
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.notifications (id, event_id, user_id, display_date, disabled) FROM stdin;
177	19	laura.deac@gmail.com	2022-01-18	f
178	19	laura.deac@gmail.com	2022-01-17	f
179	19	laura.deac@gmail.com	2022-01-15	f
180	18	laura.deac@gmail.com	2022-01-18	f
181	18	laura.deac@gmail.com	2022-01-17	f
182	18	laura.deac@gmail.com	2022-01-15	f
97	14	george.giosan@gmail.com	2022-01-15	f
98	16	george.giosan@gmail.com	2022-08-27	f
99	16	george.giosan@gmail.com	2022-08-26	f
100	16	george.giosan@gmail.com	2022-08-24	f
104	20	george.giosan@gmail.com	2022-01-22	f
105	20	george.giosan@gmail.com	2022-01-21	f
106	20	george.giosan@gmail.com	2022-01-19	f
107	18	george.giosan@gmail.com	2022-01-18	f
108	18	george.giosan@gmail.com	2022-01-17	f
109	18	george.giosan@gmail.com	2022-01-15	f
111	17	george.giosan@gmail.com	2022-01-15	f
95	14	george.giosan@gmail.com	2022-01-18	t
96	14	george.giosan@gmail.com	2022-01-17	t
110	17	george.giosan@gmail.com	2022-01-16	t
101	13	george.giosan@gmail.com	2022-01-28	t
102	13	george.giosan@gmail.com	2022-01-27	t
103	13	george.giosan@gmail.com	2022-01-25	t
183	13	laura.deac@gmail.com	2022-01-28	f
184	13	laura.deac@gmail.com	2022-01-27	f
112	17	stefan.farcasanu@gmail.com	2022-01-16	f
113	17	stefan.farcasanu@gmail.com	2022-01-15	f
114	14	stefan.farcasanu@gmail.com	2022-01-18	f
115	14	stefan.farcasanu@gmail.com	2022-01-17	f
116	14	stefan.farcasanu@gmail.com	2022-01-15	f
117	18	stefan.farcasanu@gmail.com	2022-01-18	f
118	18	stefan.farcasanu@gmail.com	2022-01-17	f
119	18	stefan.farcasanu@gmail.com	2022-01-15	f
120	19	stefan.farcasanu@gmail.com	2022-01-18	f
121	19	stefan.farcasanu@gmail.com	2022-01-17	f
122	19	stefan.farcasanu@gmail.com	2022-01-15	f
123	20	stefan.farcasanu@gmail.com	2022-01-22	f
124	20	stefan.farcasanu@gmail.com	2022-01-21	f
125	20	stefan.farcasanu@gmail.com	2022-01-19	f
126	13	stefan.farcasanu@gmail.com	2022-01-28	f
127	13	stefan.farcasanu@gmail.com	2022-01-27	f
128	13	stefan.farcasanu@gmail.com	2022-01-25	f
185	13	laura.deac@gmail.com	2022-01-25	f
186	15	laura.deac@gmail.com	2022-08-27	f
129	15	stefan.farcasanu@gmail.com	2022-08-27	f
130	15	stefan.farcasanu@gmail.com	2022-08-26	f
131	15	stefan.farcasanu@gmail.com	2022-08-24	f
132	16	stefan.farcasanu@gmail.com	2022-08-27	f
133	16	stefan.farcasanu@gmail.com	2022-08-26	f
134	16	stefan.farcasanu@gmail.com	2022-08-24	f
135	20	victor.doroftei@gmail.com	2022-01-22	f
136	20	victor.doroftei@gmail.com	2022-01-21	f
137	20	victor.doroftei@gmail.com	2022-01-19	f
138	14	victor.doroftei@gmail.com	2022-01-18	f
139	14	victor.doroftei@gmail.com	2022-01-17	f
140	14	victor.doroftei@gmail.com	2022-01-15	f
141	16	victor.doroftei@gmail.com	2022-08-27	f
142	16	victor.doroftei@gmail.com	2022-08-26	f
143	16	victor.doroftei@gmail.com	2022-08-24	f
144	15	victor.doroftei@gmail.com	2022-08-27	f
145	15	victor.doroftei@gmail.com	2022-08-26	f
146	15	victor.doroftei@gmail.com	2022-08-24	f
147	17	victor.gradinariu@gmail.com	2022-01-16	f
148	17	victor.gradinariu@gmail.com	2022-01-15	f
149	14	victor.gradinariu@gmail.com	2022-01-18	f
150	14	victor.gradinariu@gmail.com	2022-01-17	f
151	14	victor.gradinariu@gmail.com	2022-01-15	f
152	18	victor.gradinariu@gmail.com	2022-01-18	f
153	18	victor.gradinariu@gmail.com	2022-01-17	f
154	18	victor.gradinariu@gmail.com	2022-01-15	f
155	19	victor.gradinariu@gmail.com	2022-01-18	f
156	19	victor.gradinariu@gmail.com	2022-01-17	f
157	19	victor.gradinariu@gmail.com	2022-01-15	f
158	17	iulia.filimon@gmail.com	2022-01-16	f
159	17	iulia.filimon@gmail.com	2022-01-15	f
160	14	iulia.filimon@gmail.com	2022-01-18	f
161	14	iulia.filimon@gmail.com	2022-01-17	f
162	14	iulia.filimon@gmail.com	2022-01-15	f
163	18	iulia.filimon@gmail.com	2022-01-18	f
164	18	iulia.filimon@gmail.com	2022-01-17	f
165	18	iulia.filimon@gmail.com	2022-01-15	f
166	19	iulia.filimon@gmail.com	2022-01-18	f
167	19	iulia.filimon@gmail.com	2022-01-17	f
168	19	iulia.filimon@gmail.com	2022-01-15	f
169	15	iulia.filimon@gmail.com	2022-08-27	f
170	15	iulia.filimon@gmail.com	2022-08-26	f
171	15	iulia.filimon@gmail.com	2022-08-24	f
172	16	iulia.filimon@gmail.com	2022-08-27	f
173	16	iulia.filimon@gmail.com	2022-08-26	f
174	16	iulia.filimon@gmail.com	2022-08-24	f
175	17	laura.deac@gmail.com	2022-01-16	f
176	17	laura.deac@gmail.com	2022-01-15	f
187	15	laura.deac@gmail.com	2022-08-26	f
188	15	laura.deac@gmail.com	2022-08-24	f
189	14	laura.deac@gmail.com	2022-01-18	f
190	14	laura.deac@gmail.com	2022-01-17	f
191	14	laura.deac@gmail.com	2022-01-15	f
192	17	mihai.dretcanu@gmail.com	2022-01-16	f
193	17	mihai.dretcanu@gmail.com	2022-01-15	f
194	19	mihai.dretcanu@gmail.com	2022-01-18	f
195	19	mihai.dretcanu@gmail.com	2022-01-17	f
196	19	mihai.dretcanu@gmail.com	2022-01-15	f
197	15	mihai.dretcanu@gmail.com	2022-08-27	f
198	15	mihai.dretcanu@gmail.com	2022-08-26	f
199	15	mihai.dretcanu@gmail.com	2022-08-24	f
200	16	mihai.dretcanu@gmail.com	2022-08-27	f
201	16	mihai.dretcanu@gmail.com	2022-08-26	f
202	16	mihai.dretcanu@gmail.com	2022-08-24	f
203	17	catalin.halbes@gmail.com	2022-01-16	f
204	17	catalin.halbes@gmail.com	2022-01-15	f
205	19	catalin.halbes@gmail.com	2022-01-18	f
206	19	catalin.halbes@gmail.com	2022-01-17	f
207	19	catalin.halbes@gmail.com	2022-01-15	f
208	20	catalin.halbes@gmail.com	2022-01-22	f
209	20	catalin.halbes@gmail.com	2022-01-21	f
210	20	catalin.halbes@gmail.com	2022-01-19	f
211	18	catalin.halbes@gmail.com	2022-01-18	f
212	18	catalin.halbes@gmail.com	2022-01-17	f
213	18	catalin.halbes@gmail.com	2022-01-15	f
214	17	ionela.gagea@gmail.com	2022-01-16	f
215	17	ionela.gagea@gmail.com	2022-01-15	f
216	14	ionela.gagea@gmail.com	2022-01-18	f
217	14	ionela.gagea@gmail.com	2022-01-17	f
218	14	ionela.gagea@gmail.com	2022-01-15	f
219	18	ionela.gagea@gmail.com	2022-01-18	f
220	18	ionela.gagea@gmail.com	2022-01-17	f
221	18	ionela.gagea@gmail.com	2022-01-15	f
222	15	ionela.gagea@gmail.com	2022-08-27	f
223	15	ionela.gagea@gmail.com	2022-08-26	f
224	15	ionela.gagea@gmail.com	2022-08-24	f
225	20	ionela.gagea@gmail.com	2022-01-22	f
226	20	ionela.gagea@gmail.com	2022-01-21	f
227	20	ionela.gagea@gmail.com	2022-01-19	f
228	17	maria.ghita@gmail.com	2022-01-16	f
229	17	maria.ghita@gmail.com	2022-01-15	f
230	14	maria.ghita@gmail.com	2022-01-18	f
231	14	maria.ghita@gmail.com	2022-01-17	f
232	14	maria.ghita@gmail.com	2022-01-15	f
233	18	maria.ghita@gmail.com	2022-01-18	f
234	18	maria.ghita@gmail.com	2022-01-17	f
235	18	maria.ghita@gmail.com	2022-01-15	f
236	17	andrei.back@gmail.com	2022-01-16	f
237	17	andrei.back@gmail.com	2022-01-15	f
238	14	andrei.back@gmail.com	2022-01-18	f
239	14	andrei.back@gmail.com	2022-01-17	f
240	14	andrei.back@gmail.com	2022-01-15	f
241	18	andrei.back@gmail.com	2022-01-18	f
242	18	andrei.back@gmail.com	2022-01-17	f
243	18	andrei.back@gmail.com	2022-01-15	f
244	19	andrei.back@gmail.com	2022-01-18	f
245	19	andrei.back@gmail.com	2022-01-17	f
246	19	andrei.back@gmail.com	2022-01-15	f
247	20	andrei.back@gmail.com	2022-01-22	f
248	20	andrei.back@gmail.com	2022-01-21	f
249	20	andrei.back@gmail.com	2022-01-19	f
250	13	andrei.back@gmail.com	2022-01-28	f
251	13	andrei.back@gmail.com	2022-01-27	f
252	13	andrei.back@gmail.com	2022-01-25	f
253	15	andrei.back@gmail.com	2022-08-27	f
254	15	andrei.back@gmail.com	2022-08-26	f
255	15	andrei.back@gmail.com	2022-08-24	f
256	16	andrei.back@gmail.com	2022-08-27	f
257	16	andrei.back@gmail.com	2022-08-26	f
258	16	andrei.back@gmail.com	2022-08-24	f
\.


--
-- Data for Name: recipients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.recipients (user_id, message_id) FROM stdin;
andrei.back@gmail.com	111
george.giosan@gmail.com	112
andrei.back@gmail.com	113
george.giosan@gmail.com	114
mihai.dretcanu@gmail.com	115
catalin.halbes@gmail.com	115
george.giosan@gmail.com	115
laura.deac@gmail.com	115
laura.deac@gmail.com	116
stefan.farcasanu@gmail.com	116
catalin.halbes@gmail.com	116
george.giosan@gmail.com	116
stefan.farcasanu@gmail.com	117
mihai.dretcanu@gmail.com	118
laura.deac@gmail.com	118
stefan.farcasanu@gmail.com	118
catalin.halbes@gmail.com	118
george.giosan@gmail.com	119
stefan.farcasanu@gmail.com	120
george.giosan@gmail.com	121
stefan.farcasanu@gmail.com	122
catalin.halbes@gmail.com	123
george.giosan@gmail.com	124
catalin.halbes@gmail.com	126
george.giosan@gmail.com	127
andrei.grigorescu@gmail.com	128
george.giosan@gmail.com	129
andrei.grigorescu@gmail.com	130
george.giosan@gmail.com	131
mara.gheorghe@gmail.com	132
george.giosan@gmail.com	133
mara.gheorghe@gmail.com	134
george.giosan@gmail.com	135
victor.gradinariu@gmail.com	136
george.giosan@gmail.com	137
victor.gradinariu@gmail.com	138
george.giosan@gmail.com	139
victor.gradinariu@gmail.com	140
mihai.dretcanu@gmail.com	141
george.giosan@gmail.com	142
mihai.dretcanu@gmail.com	143
george.giosan@gmail.com	144
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (username, password, firstname, lastname, salt, lastloginat) FROM stdin;
andrei.grigorescu@gmail.com	0iccv4tptraAZIppv6Fmy3HmHRYCtEP6uRqja0sQHyw=	Andrei	Grigorescu	HFzVXpGcIBpyVK1Sfffwg5LjXRop9zk8	2022-01-15
mara.gheorghe@gmail.com	mMqj4Fic8BAaf9RR30kAiPgunmpLQrDgSFHsJRuMl8U=	Mara	Gheorghe	Mj3wokf0r1PyUaZn3dB2IULAoP566sJV	2022-01-15
livius.habuc@gmail.com	6BW0kFAas+fdRqTLUEua+99BlMInTYKUL91tAsybONw=	Livius	Habuc	elngZDUeqJuYiSYmtzTO77eGhmU4oqlM	2022-01-15
stefan.farcasanu@gmail.com	5P9G5bVvvAnQz2OcuP4snN8rdBhv/nAVIIVa6hYmS6Q=	Stefan	Farcasanu	sF0alCDuGNOdB8Km0Ty45z5JAxbJQG6Z	2022-01-15
victor.doroftei@gmail.com	3Gvz0dJ6+xBzKEPxPh44jIy6Y4SV7XI3x3yMxjwzOzI=	Victor	Doroftei	3Qr4BIvlhGGDIxBGuhmtHzXkbAaHeALP	2022-01-15
victor.gradinariu@gmail.com	T3HwC4T55RH36zhl28z7f3sOMi0c1E9/Nxay5f1Iu9A=	Victor	Gradinariu	QuQid6b1tofMXumTlWRTopTLhdkAxA9l	2022-01-15
iulia.filimon@gmail.com	jkNRA7j5TDhreuLBYRccXbWwU0ufl1a0QbEZuFltmbI=	Iulia	Filimon	cCxESXFyAgI1Vz9Dx5guAssYWEtdAMR1	2022-01-15
laura.deac@gmail.com	M5cQwUwcd/hn4OPIPG9Tj3tzjQ+FsAzaifmeNhEm8ls=	Laura	Deac	6cDbnsusgLaMVz9voRzGw9IdACzyeCUz	2022-01-15
mihai.dretcanu@gmail.com	P48vxrd3QfS+Db+UBYzwu4FyDTqWaYhuc8z61/S3kes=	Mihai	Dretcanu	JDk6UKOesHBc4FQwTgouk5WlnBxyBREw	2022-01-15
catalin.halbes@gmail.com	L+blg8HkzUzCLL5lqWkyDUHJwHSPLmbmUUEhrQtejIU=	Catalin	Halbes	Ts5wxMt6h1VQzA3ZabTm45jRDGQYfeUL	2022-01-15
ionela.gagea@gmail.com	3RsIesaKTVYpeCsbhsO11xZvl0lfvOOYLpMkYvvcM34=	Ionela	Gagea	dY3fg2yXATU67HQeFykXB5zjEUhYvOn0	2022-01-15
maria.ghita@gmail.com	O3hEojZzIfp7gI3SjeiTymqhbbboH0zDz0DpdGSjt44=	Maria	Ghita	WTWKsQbUwRKruTJJY7D25fThnySpitqS	2022-01-15
andrei.back@gmail.com	V4IXMRKQyiwtPgexRw9ZL71sMJ8rfsG1CNk4ndyJALc=	Andrei	Back	q1bkA9RziQqgiDXMlwL3Kb8wFvREUUIi	2022-01-15
flavia.dorobat@gmail.com	hku3jdphdbSIq9VjHcY74djpxho7UOdqQeTy+DBZu9g=	Flavia	Dorobat	dLl8ZnX0XpnNde6OmaUzscoXPPhOluw2	\N
paula.gal@gmail.com	J53bAerfi18/OAN9e6xNTk6DfupsGod1RZfuqT7mz7E=	Paula	Gal	7UoZL6MLKwrKWhAZsO37jsz5Jhn3F3Fw	\N
george.giosan@gmail.com	34e4glTYM0zfKyQioIRraYxJaXVG8l4kdStQXKZD0Bw=	George	Giosan	lQNCzQZwYyqUSP7CiOjMFkTr90DyYt9z	2022-01-15
\.


--
-- Name: events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.events_id_seq', 20, true);


--
-- Name: messages_group_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messages_group_seq', 44, true);


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messages_id_seq', 144, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.notifications_id_seq', 258, true);


--
-- Name: event_participants event_participants_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_participants_pk PRIMARY KEY (event_id, user_id);


--
-- Name: events events_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pk PRIMARY KEY (id);


--
-- Name: friendship_requests friendship_requests_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendship_requests
    ADD CONSTRAINT friendship_requests_pk PRIMARY KEY (from_username, to_username);


--
-- Name: messages messages_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pk PRIMARY KEY (id);


--
-- Name: notifications notifications_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pk PRIMARY KEY (id);


--
-- Name: friendships pk_friendship; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT pk_friendship PRIMARY KEY (user1, user2);


--
-- Name: recipients pk_recipients; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recipients
    ADD CONSTRAINT pk_recipients PRIMARY KEY (user_id, message_id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (username);


--
-- Name: notifications_id_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX notifications_id_uindex ON public.notifications USING btree (id);


--
-- Name: event_participants event_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES public.events(id) ON DELETE CASCADE;


--
-- Name: notifications event_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES public.events(id) ON DELETE CASCADE;


--
-- Name: recipients fk_message; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recipients
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- Name: messages fk_replies_to; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT fk_replies_to FOREIGN KEY (replies_to) REFERENCES public.messages(id) ON DELETE SET NULL;


--
-- Name: recipients fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.recipients
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- Name: friendships fk_user1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT fk_user1 FOREIGN KEY (user1) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- Name: friendships fk_user2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT fk_user2 FOREIGN KEY (user2) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- Name: friendship_requests friendship_requests_fk_from; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendship_requests
    ADD CONSTRAINT friendship_requests_fk_from FOREIGN KEY (from_username) REFERENCES public.users(username) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: friendship_requests friendship_requests_fk_to; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendship_requests
    ADD CONSTRAINT friendship_requests_fk_to FOREIGN KEY (to_username) REFERENCES public.users(username) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: messages messages_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_fk FOREIGN KEY (from_user) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- Name: event_participants user_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_participants
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- Name: notifications user_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES public.users(username) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

