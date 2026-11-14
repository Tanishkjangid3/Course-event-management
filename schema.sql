-- ========================================================
-- CampusCourse Events Database Schema and Seed Data (20 events, 33 registrations)
-- Database: course_event_management
-- Author: Senior Java Developer
-- Current Reference Date: 31 October 2026
-- ========================================================

CREATE DATABASE IF NOT EXISTS course_event_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_event_management;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS courses;

-- --------------------------------------------------------
-- Table: courses
-- --------------------------------------------------------
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    description TEXT,
    image_path VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: users
-- --------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    enrollment_no VARCHAR(50),
    course_id INT NULL,
    year VARCHAR(20) NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', -- 'ADMIN' or 'STUDENT'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: events
-- --------------------------------------------------------
CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    course_id INT NULL,
    description TEXT,
    venue VARCHAR(100) NOT NULL,
    building VARCHAR(100) NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    registration_deadline DATETIME NOT NULL,
    total_seats INT NOT NULL DEFAULT 50,
    fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    organizer VARCHAR(100) NOT NULL,
    contact VARCHAR(100) NOT NULL,
    thumbnail_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Table: registrations
-- --------------------------------------------------------
CREATE TABLE registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reg_no VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('REGISTERED', 'ATTENDED', 'CANCELLED') NOT NULL DEFAULT 'REGISTERED',
    requirements TEXT,
    UNIQUE KEY uq_user_event (user_id, event_id),
    CONSTRAINT fk_reg_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- SEED DATA
-- ========================================================

-- 1. Courses (6)
INSERT INTO courses (id, name, department, duration, description, image_path) VALUES
(1, 'B.Tech Computer Science', 'Computer Science & Engineering', '4 Years', 'Comprehensive curriculum covering algorithms, software engineering, AI, systems programming, and modern distributed computing.', 'images/course1.jpg'),
(2, 'B.Tech Information Technology', 'Information Technology', '4 Years', 'Specialized program emphasizing cloud infrastructure, network security, web architecture, and full-stack enterprise solutions.', 'images/course2.jpg'),
(3, 'BCA', 'Computer Applications', '3 Years', 'Undergraduate foundation program focusing on application development, database management, web technologies, and systems design.', 'images/course3.jpg'),
(4, 'MCA', 'Computer Applications', '2 Years', 'Advanced post-graduate studies in enterprise software development, distributed architectures, cloud platforms, and cyber intelligence.', 'images/course4.jpg'),
(5, 'MBA', 'Management Studies', '2 Years', 'Executive management program developing leadership, product strategy, startup entrepreneurship, marketing, and corporate finance.', 'images/course5.jpg'),
(6, 'B.Sc Data Science', 'Mathematical & Data Sciences', '3 Years', 'Interdisciplinary program integrating advanced statistics, machine learning, predictive modeling, big data tools, and data visualization.', 'images/course6.jpg');

-- 2. Users (1 Admin, 1 Demo Student, 8 other students)
-- Password for Admin is Admin@123
-- Password for Student is Student@123
-- Password for others is Password@123
INSERT INTO users (id, full_name, email, phone, enrollment_no, course_id, year, password_hash, role, created_at) VALUES
(1, 'System Administrator', 'admin@campus.com', '9876543210', 'ADM-2024-001', NULL, 'Faculty/Staff', 'c4ca4238a0b923820dcc509a6f75849b:34388280d67a5f30a967d2701d0bdaebeca05f2ac564a52e090eac157bdaf2b1', 'ADMIN', '2026-08-01 09:00:00'),
(2, 'Aarav Sharma', 'student@campus.com', '9876543220', 'ENR-2024-1001', 1, '3rd Year', 'c4ca4238a0b923820dcc509a6f75849b:f6f1d084ba22b5146402c9121d2cba248e7f1bdfd14214087c64b1751bfb41a8', 'STUDENT', '2026-08-15 10:30:00'),
(3, 'Rohan Verma', 'rohan.verma@campus.com', '9876543221', 'ENR-2024-1002', 2, '3rd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-16 11:00:00'),
(4, 'Priya Singh', 'priya.singh@campus.com', '9876543222', 'ENR-2024-1003', 6, '2nd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-17 11:30:00'),
(5, 'Sneha Patel', 'sneha.patel@campus.com', '9876543223', 'ENR-2024-1004', 3, '2nd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-18 12:00:00'),
(6, 'Aman Gupta', 'aman.gupta@campus.com', '9876543224', 'ENR-2024-1005', 4, '1st Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-19 14:00:00'),
(7, 'Pooja Iyer', 'pooja.iyer@campus.com', '9876543225', 'ENR-2024-1006', 5, '2nd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-20 15:30:00'),
(8, 'Aditya Joshi', 'aditya.joshi@campus.com', '9876543226', 'ENR-2024-1007', 1, '4th Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-21 16:00:00'),
(9, 'Ananya Roy', 'ananya.roy@campus.com', '9876543227', 'ENR-2024-1008', 2, '2nd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-22 16:30:00'),
(10, 'Kunal Deshmukh', 'kunal.deshmukh@campus.com', '9876543228', 'ENR-2024-1009', 6, '3rd Year', 'c4ca4238a0b923820dcc509a6f75849b:7ae5213502f00dc2b79f53c12ae14b45c097e36c3a6b5d6b4bcc965fb3dede9f', 'STUDENT', '2026-08-23 17:00:00');

-- 3. Events (12 Events: 4 Past, 1 Ongoing, 7 Upcoming)
-- Today is 2026-10-31
INSERT INTO events (id, title, course_id, description, venue, building, start_datetime, end_datetime, registration_deadline, total_seats, fee, organizer, contact, thumbnail_path, created_at) VALUES
(1, 'Python Bootcamp for Beginners', 1,
 'An intensive, hands-on weekend bootcamp introducing core Python programming fundamentals, data structures, and script automation. Students completed coding challenges and built basic command-line applications under guided mentor supervision.',
 'Computer Lab 3', 'Turing Block', '2026-10-05 09:30:00', '2026-10-05 16:30:00', '2026-10-03 23:59:00', 40, 0.00, 'Prof. Rajesh Sharma', 'rajesh.cs@campus.edu', 'images/event1.jpg', '2026-09-20 10:00:00'),

(2, 'Web Development Workshop', 2,
 'Master contemporary responsive web design, semantic HTML5, modern CSS Grid/Flexbox layouts, and dynamic client-side scripting. Participants created interactive personal portfolio sites and deployed them live to web hosting environments.',
 'Software Lab 1', 'Tech Tower', '2026-10-12 10:00:00', '2026-10-12 17:00:00', '2026-10-10 23:59:00', 45, 150.00, 'Dr. Anjali Mehta', 'anjali.it@campus.edu', 'images/event2.jpg', '2026-09-25 11:30:00'),

(3, 'Tech Quiz Championship', 3,
 'Annual inter-departmental technology quiz test pitting college minds against challenging rounds of computer trivia, algorithms, history of tech, and rapid-fire debugging. Cash prizes and trophies awarded to top teams.',
 'Seminar Hall A', 'Science Complex', '2026-10-18 11:00:00', '2026-10-18 15:00:00', '2026-10-16 23:59:00', 60, 0.00, 'Prof. Vikram Joshi', 'vikram.bca@campus.edu', 'images/event3.jpg', '2026-10-01 09:00:00'),

(4, 'Industry Expert Talk on Cloud Careers', 4,
 'Senior cloud architects from leading multi-national enterprises discussed real-world architectural paradigms, high availability, container orchestration with Kubernetes, and roadmap strategies for clearing AWS/GCP certifications.',
 'Main Auditorium', 'Admin Block', '2026-10-24 14:00:00', '2026-10-24 17:00:00', '2026-10-22 23:59:00', 120, 0.00, 'Dr. S. Ramanathan', 'ramanathan.mca@campus.edu', 'images/event4.jpg', '2026-10-05 14:00:00'),

(5, 'AI & Machine Learning Hackathon', 1,
 'A high-energy, 2-day hackathon where interdisciplinary student teams formulate and prototype machine learning models addressing healthcare, agriculture, and urban automation problems. Industry mentors and compute resources provided throughout.',
 'Innovation Hub, Block C Room 204', 'Block C', '2026-10-30 09:00:00', '2026-10-31 19:00:00', '2026-10-28 23:59:00', 50, 200.00, 'Dr. Neha Kapoor', 'neha.ai@campus.edu', 'images/event5.jpg', '2026-10-10 10:00:00'),

(6, 'Google Cloud Study Jam', 2,
 'Interactive study jam guiding students through hands-on cloud labs, IAM security, BigQuery analytics, and Compute Engine deployment. Earn Google Cloud digital skill badges and access free Qwiklabs credits during the workshop.',
 'Cloud Computing Lab', 'Tech Tower', '2026-11-05 10:00:00', '2026-11-05 16:00:00', '2026-11-03 23:59:00', 50, 0.00, 'Prof. Karan Patel', 'karan.cloud@campus.edu', 'images/event6.jpg', '2026-10-15 11:00:00'),

(7, 'Data Science Seminar with Industry Guests', 6,
 'Renowned data scientists demonstrate real-time predictive analytics pipelines, deep neural network training techniques, and ethical AI implications. Includes open Q&A session and project portfolio showcase for graduating students.',
 'Seminar Hall B', 'Ramanujan Block', '2026-11-12 10:30:00', '2026-11-12 15:30:00', '2026-11-10 23:59:00', 80, 100.00, 'Dr. Priya Nair', 'priya.ds@campus.edu', 'images/event7.jpg', '2026-10-18 12:00:00'),

(8, 'Cybersecurity Awareness Workshop', 3,
 'Practical cybersecurity training highlighting ethical hacking, vulnerability scanning, network traffic analysis with Wireshark, phishing mitigation, and defending against common modern threat vectors in campus networks.',
 'Cyber Security Lab', 'Block B Room 102', '2026-11-18 11:00:00', '2026-11-18 16:00:00', '2026-11-16 23:59:00', 60, 50.00, 'Prof. Amit Verma', 'amit.cyber@campus.edu', 'images/event8.jpg', '2026-10-20 09:30:00'),

(9, 'Entrepreneurship and Startup Pitch Day', 5,
 'Budding student founders pitch innovative technology and service ventures to active angel investors, venture partners, and incubators. The top three winning startup pitches will secure seed grant funding and 6-month lab incubation.',
 'Executive Hall', 'Management Block', '2026-11-25 09:30:00', '2026-11-25 17:00:00', '2026-11-22 23:59:00', 70, 150.00, 'Prof. Sunita Deshmukh', 'sunita.mba@campus.edu', 'images/event9.jpg', '2026-10-22 10:15:00'),

(10, 'Placement Preparation Aptitude Camp', 4,
 'Intensive corporate readiness camp covering quantitative problem solving, logical reasoning patterns, technical mock interviews, resume optimization, and group discussion simulations led by corporate recruiters.',
 'Central Seminar Hall', 'Placement Block', '2026-12-02 09:00:00', '2026-12-03 17:00:00', '2026-11-28 23:59:00', 100, 0.00, 'Training & Placement Officer', 'placement@campus.edu', 'images/event10.jpg', '2026-10-25 15:00:00'),

(11, 'Robotics & IoT Hands-On Workshop', 2,
 'Hands-on hardware lab interfacing Arduino, ESP32 microcontrollers, sensor suites, and MQTT telemetry to assemble smart IoT environment monitors. (Note: Registration for this batch closed early due to hardware inventory allocation).',
 'Embedded Systems Lab', 'Engineering Block', '2026-11-15 10:00:00', '2026-11-15 16:00:00', '2026-10-25 23:59:00', 30, 300.00, 'Prof. Sandeep Kulkarni', 'sandeep.iot@campus.edu', 'images/event11.jpg', '2026-10-10 14:00:00'),

(12, 'Advanced Full-Stack Engineering Masterclass', 1,
 'Deep-dive micro-batch masterclass on designing reactive microservices, secure token management, distributed caching, and zero-downtime container deployments. Intentionally limited seat capacity for high-touch instructor coaching.',
 'Lab 5', 'Tech Tower', '2026-11-20 10:00:00', '2026-11-20 17:00:00', '2026-11-18 23:59:00', 8, 250.00, 'Er. Rajesh Soni', 'rajesh.soni@campus.edu', 'images/event1.jpg', '2026-10-12 16:00:00');

-- 3b. New upcoming events (Dec 2026 - Mar 2027)
INSERT INTO events (id, title, course_id, description, venue, building, start_datetime, end_datetime, registration_deadline, total_seats, fee, organizer, contact, thumbnail_path, created_at) VALUES
(13, 'Flutter App Development Workshop', 2,
 'Hands-on workshop on building cross-platform mobile apps with Flutter and Dart. Participants design screens, manage state, connect to a REST API and run their app on an emulator by the end of the day. Laptops with Flutter SDK installed are recommended.',
 'Software Lab 2', 'Tech Tower', '2026-12-09 10:00:00', '2026-12-09 16:00:00', '2026-12-07 23:59:00', 40, 100.00, 'Prof. Meera Kulkarni', 'meera.mobile@campus.edu', 'images/event13.jpg', '2026-11-10 10:00:00'),

(14, 'Competitive Programming Contest', 1,
 'A four-hour individual coding contest with algorithmic problems on arrays, graphs, dynamic programming and number theory. Ranked on a live scoreboard with certificates and trophies for the top ten participants.',
 'Computer Lab 1', 'Turing Block', '2026-12-13 11:00:00', '2026-12-13 15:00:00', '2026-12-11 23:59:00', 80, 0.00, 'Prof. Rajesh Sharma', 'rajesh.cs@campus.edu', 'images/event14.jpg', '2026-11-10 11:00:00'),

(15, 'Data Analytics with Power BI', 6,
 'Learn to clean data, build interactive dashboards and tell stories with Power BI. The session covers data modelling, DAX basics, charts and publishing a report, using real campus and retail datasets.',
 'Analytics Lab', 'Ramanujan Block', '2026-12-17 10:00:00', '2026-12-17 16:30:00', '2026-12-15 23:59:00', 45, 120.00, 'Dr. Priya Nair', 'priya.ds@campus.edu', 'images/event15.jpg', '2026-11-10 12:00:00'),

(16, 'Digital Marketing and Brand Strategy Masterclass', 5,
 'A practical masterclass on social media campaigns, SEO basics, content planning and brand positioning. Industry marketers share real campaign case studies and run a live brand-pitch exercise for student teams.',
 'Executive Hall', 'Management Block', '2027-01-08 10:30:00', '2027-01-08 16:00:00', '2027-01-05 23:59:00', 60, 150.00, 'Prof. Sunita Deshmukh', 'sunita.mba@campus.edu', 'images/event16.jpg', '2026-11-11 09:30:00'),

(17, 'Ethical Hacking CTF Challenge', 3,
 'A full-day Capture The Flag event with beginner-friendly challenges in web exploitation, cryptography, forensics and reverse engineering. Teams of two to three compete on a live leaderboard inside an isolated lab network.',
 'Cyber Security Lab', 'Block B Room 102', '2027-01-16 09:00:00', '2027-01-16 18:00:00', '2027-01-13 23:59:00', 50, 50.00, 'Prof. Amit Verma', 'amit.cyber@campus.edu', 'images/event17.jpg', '2026-11-11 10:30:00'),

(18, 'TechNova 2027 Annual Tech Fest', NULL,
 'Our two-day annual technical festival with project exhibitions, coding and robotics competitions, a gaming arena, startup demos and guest talks from industry leaders. Open to students of all courses.',
 'Main Campus Ground', 'Admin Block', '2027-02-05 09:30:00', '2027-02-06 17:00:00', '2027-02-01 23:59:00', 300, 100.00, 'Student Council', 'technova@campus.edu', 'images/event18.jpg', '2026-11-12 09:00:00'),

(19, 'Resume and Interview Skills for Freshers', 4,
 'Placement-cell mentors and recent alumni walk through building a strong resume, preparing for technical and HR rounds, and handling group discussions. Includes live mock interviews with personal feedback.',
 'Main Auditorium', 'Admin Block', '2027-02-19 14:00:00', '2027-02-19 17:00:00', '2027-02-17 23:59:00', 120, 0.00, 'Training & Placement Officer', 'placement@campus.edu', 'images/event19.jpg', '2026-11-12 11:00:00'),

(20, 'Generative AI and Prompt Engineering Seminar', 6,
 'An introductory seminar on how large language models work, writing effective prompts, building simple AI-powered tools and using AI responsibly in study and work. Includes a live demo session and open Q&A.',
 'Seminar Hall B', 'Ramanujan Block', '2027-03-06 10:00:00', '2027-03-06 15:00:00', '2027-03-03 23:59:00', 100, 0.00, 'Dr. Neha Kapoor', 'neha.ai@campus.edu', 'images/event20.jpg', '2026-11-13 10:00:00');

-- 4. Registrations (17 Registrations)
-- Event 12 has 8 seats total, and we register 8 students so it has 0 seats left (Full!)
-- Student 2 (student@campus.com) has registrations across past, ongoing, and upcoming events.
INSERT INTO registrations (id, reg_no, user_id, event_id, registered_on, status, requirements) VALUES
-- Past event registrations
(1, 'EVT-2026-0001', 2, 1, '2026-09-22 14:10:00', 'ATTENDED', 'Needs Python 3 environment setup on lab system'),
(2, 'EVT-2026-0002', 3, 2, '2026-09-26 10:20:00', 'ATTENDED', 'Bringing own laptop with VS Code'),
(3, 'EVT-2026-0003', 5, 3, '2026-10-02 12:00:00', 'ATTENDED', 'Participating in team Alpha'),
(4, 'EVT-2026-0004', 6, 4, '2026-10-08 15:30:00', 'ATTENDED', 'Interested in cloud certification guidance'),

-- Ongoing event registrations (Event 5)
(5, 'EVT-2026-0005', 2, 5, '2026-10-12 11:45:00', 'REGISTERED', 'Require high-GPU compute workstation allocation'),
(6, 'EVT-2026-0006', 8, 5, '2026-10-14 16:20:00', 'REGISTERED', 'Team NeuralForge lead'),

-- Upcoming event registrations (Event 6, 7, 8, 9)
(7, 'EVT-2026-0007', 2, 6, '2026-10-16 09:15:00', 'REGISTERED', 'Eager to earn GCP Cloud Architect badge'),
(8, 'EVT-2026-0008', 3, 6, '2026-10-17 14:40:00', 'REGISTERED', 'Require Qwiklabs activation support'),
(9, 'EVT-2026-0009', 4, 7, '2026-10-19 11:10:00', 'REGISTERED', 'Presenting poster on natural language processing'),
(10, 'EVT-2026-0010', 10, 7, '2026-10-20 15:50:00', 'REGISTERED', 'None'),
(11, 'EVT-2026-0011', 7, 9, '2026-10-23 10:00:00', 'REGISTERED', 'Pitch deck ready for smart city logistics'),
(12, 'EVT-2026-0012', 4, 1, '2026-09-24 16:30:00', 'CANCELLED', 'Schedule conflict with university exam'),

-- Event 12 (Capacity = 8, Fill all 8 seats so it is 100% full!)
(13, 'EVT-2026-0013', 3, 12, '2026-10-13 09:00:00', 'REGISTERED', 'Docker installed on personal laptop'),
(14, 'EVT-2026-0014', 4, 12, '2026-10-13 10:15:00', 'REGISTERED', 'None'),
(15, 'EVT-2026-0015', 5, 12, '2026-10-13 11:30:00', 'REGISTERED', 'Requesting front-row seating'),
(16, 'EVT-2026-0016', 6, 12, '2026-10-13 13:00:00', 'REGISTERED', 'Pre-installed Kubernetes CLI'),
(17, 'EVT-2026-0017', 7, 12, '2026-10-13 14:15:00', 'REGISTERED', 'None'),
(18, 'EVT-2026-0018', 8, 12, '2026-10-13 15:20:00', 'REGISTERED', 'None'),
(19, 'EVT-2026-0019', 9, 12, '2026-10-13 16:40:00', 'REGISTERED', 'None'),
(20, 'EVT-2026-0020', 10, 12, '2026-10-13 17:05:00', 'REGISTERED', 'None');

-- 5. Registrations for the new events
INSERT INTO registrations (id, reg_no, user_id, event_id, registered_on, status, requirements) VALUES
(21, 'EVT-2026-0021', 2, 13, '2026-11-12 09:10:00', 'REGISTERED', 'Will bring own laptop with Flutter SDK'),
(22, 'EVT-2026-0022', 3, 13, '2026-11-12 10:25:00', 'REGISTERED', 'None'),
(23, 'EVT-2026-0023', 2, 14, '2026-11-12 11:00:00', 'REGISTERED', 'Prefers C++ for contest'),
(24, 'EVT-2026-0024', 8, 14, '2026-11-12 12:30:00', 'REGISTERED', 'None'),
(25, 'EVT-2026-0025', 10, 14, '2026-11-12 14:05:00', 'REGISTERED', 'None'),
(26, 'EVT-2026-0026', 4, 15, '2026-11-13 09:40:00', 'REGISTERED', 'Needs Power BI Desktop installed on lab system'),
(27, 'EVT-2026-0027', 10, 15, '2026-11-13 10:20:00', 'REGISTERED', 'None'),
(28, 'EVT-2026-0028', 3, 17, '2026-11-13 11:15:00', 'REGISTERED', 'Team of three: CyberCats'),
(29, 'EVT-2026-0029', 9, 17, '2026-11-13 12:00:00', 'REGISTERED', 'None'),
(30, 'EVT-2026-0030', 2, 18, '2026-11-13 13:30:00', 'REGISTERED', 'Interested in project exhibition stall'),
(31, 'EVT-2026-0031', 5, 18, '2026-11-13 14:10:00', 'REGISTERED', 'None'),
(32, 'EVT-2026-0032', 7, 18, '2026-11-13 15:45:00', 'REGISTERED', 'Showcasing startup demo'),
(33, 'EVT-2026-0033', 4, 20, '2026-11-13 16:20:00', 'REGISTERED', 'None');
