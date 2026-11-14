# CampusCourse Events — College Course Event Management System

**CampusCourse Events** is a modern, full-featured desktop application designed for college departments, academic event coordinators, faculty, and students to browse, schedule, manage, and register for workshops, hackathons, seminars, and placement camps.

---

## Key Highlights & Architectural Features

- **Strict Swing/AWT UI**: 100% native Java desktop GUI built with modern **FlatLaf (Light Theme)** adhering to Google's design palette:
  - Blue (`#1A73E8`), Green (`#34A853`), Yellow (`#FBBC04`), Red (`#EA4335`), Background (`#F8F9FA`), and crisp White cards (`#FFFFFF`).
  - Interactive hand cursors and hover highlights on all clickable elements.
- **Single-Window CardLayout Navigation**: Smooth transitions across all pages within one `JFrame`. Zero pop-up windows for page navigation.
- **Strict Vertical-Only Scrolling**: Every page implements `Scrollable` with `getScrollableTracksViewportWidth() == true`, wrapped inside a `JScrollPane` with `HORIZONTAL_SCROLLBAR_NEVER`, unit increment 16, and auto-wrapping responsive layouts.
- **Off-EDT Concurrency**: All database read/write queries execute off the Swing Event Dispatch Thread using `SwingWorker` with visual loading indicators.
- **Overbooking Prevention & Atomicity**: Atomic database transactions (`FOR UPDATE`) prevent overbooking and race conditions when multiple students register simultaneously.
- **Dynamic Graphics2D Rendering**:
  - **Official Admission Pass**: Renders a boarding-pass/ticket with student name, event title, room & building, start time, barcode, verification badge, and registration number (e.g. `EVT-2026-0001`). Supports **"Save as PNG"** and direct **"Print"** via Java Print API.
  - **Admin Distribution Bar Chart**: Renders a live bar chart of registration counts per event on the admin dashboard.
- **Security**: Passwords hashed using **SHA-256 with cryptographic per-user salt**.
- **Self-Healing Database Manager**: Automatically verifies connectivity on boot, provides interactive troubleshooting/settings dialog if MySQL is unreachable, and automatically provisions tables and seed data if absent.

---

## Prerequisites

1. **Java Development Kit (JDK 17 or higher)**
   - Verify by running: `java -version`
2. **Apache Maven (3.8+)**
   - Verify by running: `mvn -version`
3. **MySQL Server (8.0 or 8.4+)**
   - Default port: `3306`
   - Running locally or accessible via network

---

## Setup & Installation

### Step 1: Database Initialization
Open your MySQL client or terminal and run the provided `schema.sql`:
```bash
mysql -u root -p < schema.sql
```
*(Alternatively, create the database `CREATE DATABASE campus_events;` and the application's built-in `DatabaseInitializer` will automatically seed all tables on first startup).*

### Step 2: Database Configuration
Verify or modify database credentials in `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/campus_events?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
db.username=root
db.password=Poornima@181
db.driver=com.mysql.cj.jdbc.Driver
```

### Step 3: Build & Package
Build the project and generate the executable uber-jar:
```bash
mvn clean package
```

### Step 4: Run the Application
You can run the application directly using Maven:
```bash
mvn exec:java
```
Or execute the packaged executable JAR:
```bash
java -jar target/campus-course-events-1.0.0.jar
```

---

## Default Login Credentials

| Role | Email | Password | Access Privileges |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin@campus.com` | `Admin@123` | Full admin console: add/edit/delete events, upload thumbnails, manage courses, delete/mark attended registrations, export CSV, view student directory. |
| **Demo Student** | `student@campus.com` | `Student@123` | Browse events, register, view registration passes, cancel upcoming registrations, edit profile & change password. |
| **Student 2** | `rohan.verma@campus.com` | `Password@123` | Standard student account |
| **Student 3** | `priya.singh@campus.com` | `Password@123` | Standard student account |
| **Guest** | *(No login)* | *(None)* | Browse Home, explore Courses, search Events with live filters. Login prompt appears upon clicking "Register Now". |

---

## Application Feature Walkthrough

### 1. Global Navigation Bar & Footer
- **Navbar**: Appears persistently on every page with college logo, title, quick navigation links (Home, Courses, Events, My Registrations, Admin Panel, About), and auth pill (showing active user name + role badge + Logout button, or Login/Sign Up buttons for guests).
- **Footer**: College events cell contact details, address, quick shortcuts, and copyright banner.

### 2. Home Page
- **Hero Banner**: High-resolution campus artwork with bold headline *"Discover Workshops, Seminars & Events for Your Courses"* and call-to-action buttons.
- **Live Stats Bar**: Real-time counts directly from MySQL (Total Courses, Upcoming Events, Ongoing Events, Past Events, Total Registrations).
- **Happening Now**: Highlighted alert section displaying events currently in session today (`start <= now <= end`).
- **Upcoming Events Strip**: Card preview of next 3 scheduled events with thumbnails, date/time, venue, seats remaining, and status badge.
- **How It Works**: 3-step educational cards (Choose Event, Register, Attend & Learn).
- **Previous Events Highlights**: Past 3 completed events with recaps.

### 3. Courses Catalog
- Displays all academic courses (B.Tech CS, B.Tech IT, BCA, MCA, MBA, B.Sc Data Science).
- Each card displays department, duration badge, description, and event count.
- Clicking **"Explore Events →"** immediately navigates to the Events catalog filtered for that branch.

### 4. Events Catalog & Dynamic Live Filtering
- Responsive grid of event cards with 16:9 thumbnails, course tag, venue, date/time, and real-time seats left.
- **Live Search & Filter Bar**:
  - Search by title or description keyword with live key listeners.
  - Filter by Academic Course dropdown.
  - Filter by College Venue dropdown.
  - Filter Tabs: **All Events**, **Upcoming**, **Happening Now**, and **Previous Events**.
- **Automated Status Badges**:
  - `UPCOMING` (Blue): Scheduled in future with open registration.
  - `ONGOING` (Green): Currently taking place today.
  - `COMPLETED` (Gray): End date has passed.
  - `REGISTRATION CLOSED` (Red): Registration deadline has passed before event start.
  - `SEATS FULL` (Yellow): Total seat capacity reached.

### 5. Event Details & One-Click Registration
- High-res 16:9 banner, course badge, status badge, 4–6 lines description, venue & building, timing, deadline, seats left, fee, organizer, and contact details.
- **Registration Rules**:
  - Prompts login if accessed as guest.
  - Disabled if deadline passed, event completed, or seats full.
  - Blocks duplicate registration if student is already enrolled.
  - Shows **"View Your Admission Pass"** if already enrolled.
- **Registration Flow**:
  - Confirmation dialog with pre-filled student name, email, enrollment number, and optional special requirements.
  - Atomically secures seat in database transaction.
  - Instantly pops up the **Graphics2D Admission Pass** dialog.

### 6. Official Admission Pass (Graphics2D)
- Boarding-pass format with perforated divider and admit stub.
- Includes Attendee Name, Enrollment No, Event Title, Date & Time, Room & Building, Admission Fee, unique Pass Number (`EVT-2026-XXXX`), simulated digital barcode, and verified security stamp.
- **Save as PNG**: Exports high-resolution image file to user's disk via `JFileChooser`.
- **Print Pass**: Sends pass directly to printer or PDF writer via Java `PrinterJob`.

### 7. My Registrations & Student Profile
- Table of registrations with status (`REGISTERED`, `ATTENDED`, `CANCELLED`).
- **View Admission Pass**: Opens verified pass dialog.
- **Cancel Registration**: Allowed only before event start time; displays confirmation dialog and returns the seat automatically to the pool.
- **Personal Details**: Update full name and 10-digit phone number.
- **Change Password**: Verifies current password before applying new salted SHA-256 hash.

### 8. Administrator Console (Admin Only)
- **Dashboard Overview**: Stat cards + custom Graphics2D bar chart showing registration distribution across events.
- **Manage Events**: JTable with Add / Edit / Delete. Dialog features `JSpinner` with `SpinnerDateModel` for start/end/deadline, `JFileChooser` thumbnail upload with live preview, and validation (`end > start` and `deadline <= start`).
- **View Registrations**: Filter by event combo, search query, mark attendance (`ATTENDED`), delete registration, and **Export to CSV** (compatible with Excel).
- **Manage Courses**: Add / Edit / Delete academic programs.
- **Student Directory**: Search student directory by name, email, or enrollment number.

### 9. About Page
- Mission and Vision cards, official office hours, helpdesk phone, email, and a custom **vector-styled campus map block** marking the Student Activity Center in Block B.

---

## Screenshot Checklist for Viva & Examination

When presenting or submitting screenshots, capture the following pages:
- [x] **01_home_page.png**: Hero banner, live stats cards, and "Happening Now" hackathon card.
- [x] **02_courses_page.png**: Grid of all 6 degree courses with duration badges and event counts.
- [x] **03_events_page_filters.png**: Events catalog with search, course filter, and status tabs.
- [x] **04_event_details.png**: Detailed event view with metadata grid and "Register Now" button.
- [x] **05_registration_dialog.png**: Student registration confirmation modal with pre-filled info.
- [x] **06_admission_pass_dialog.png**: Graphics2D generated admission pass with barcode and "Save as PNG".
- [x] **07_my_registrations.png**: Student registrations table with pass viewing and profile edit section.
- [x] **08_admin_dashboard.png**: Admin dashboard with stat cards and Graphics2D bar chart.
- [x] **09_admin_events_form.png**: Admin event creation dialog with date spinners and thumbnail preview.
- [x] **10_admin_registrations_csv.png**: Admin registrations table with "Export to CSV" action.
- [x] **11_about_page.png**: About page with mission cards and styled campus map block.

