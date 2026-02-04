// API Configuration
const API_BASE = '';
let authToken = null;
let currentUser = null;

// Utility Functions
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} alert-dismissible fade show`;
    toast.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.getElementById('toastContainer').appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
}

async function apiRequest(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json'
    };
    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }
    
    const options = { method, headers };
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'Request failed');
        }
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Authentication
async function login(email, password) {
    try {
        const response = await apiRequest('/api/auth/login', 'POST', { email, password });
        if (response.success && response.data.token) {
            authToken = response.data.token;
            localStorage.setItem('authToken', authToken);
            
            // Decode JWT to get user info
            const payload = JSON.parse(atob(authToken.split('.')[1]));
            currentUser = {
                email: payload.sub,
                role: payload.role || extractRoleFromToken(payload)
            };
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showToast('Login successful!', 'success');
            showDashboard();
            loadDashboardData();
        }
    } catch (error) {
        showToast('Login failed: ' + error.message, 'danger');
    }
}

function extractRoleFromToken(payload) {
    // Try to extract role from various JWT structures
    if (payload.role) return payload.role;
    if (payload.authorities) {
        const auth = payload.authorities[0];
        return auth.replace('ROLE_', '');
    }
    return 'USER';
}

function quickLogin(email) {
    document.getElementById('loginEmail').value = email;
    document.getElementById('loginPassword').value = 'password';
    document.getElementById('loginForm').dispatchEvent(new Event('submit'));
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    showLogin();
    showToast('Logged out successfully', 'info');
}

function showLogin() {
    document.getElementById('loginSection').style.display = 'flex';
    document.getElementById('dashboardSection').style.display = 'none';
    document.body.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
}

function showDashboard() {
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('dashboardSection').style.display = 'block';
    document.body.style.background = '#f1f5f9';
    
    // Update user info
    const email = currentUser?.email || 'User';
    const name = email.split('@')[0].replace('.', ' ');
    document.getElementById('userName').textContent = name.charAt(0).toUpperCase() + name.slice(1);
    document.getElementById('userAvatar').textContent = name.charAt(0).toUpperCase();
    
    // Detect role and update UI
    detectAndSetUserRole();
}

async function detectAndSetUserRole() {
    // Try to detect role by testing endpoints
    try {
        // Try teacher-specific endpoint first
        const teacherResp = await fetch(`${API_BASE}/api/teachers/me`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        if (teacherResp.ok) {
            currentUser.role = 'TEACHER';
        } else {
            // Check if student
            const studentResp = await fetch(`${API_BASE}/api/students/me`, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            });
            if (studentResp.ok) {
                currentUser.role = 'STUDENT';
            } else {
                currentUser.role = 'USER';
            }
        }
    } catch {
        currentUser.role = 'USER';
    }
    
    updateUIForRole();
}

function updateUIForRole() {
    const role = currentUser?.role || 'USER';
    const roleElement = document.getElementById('userRole');
    roleElement.textContent = role;
    roleElement.className = 'badge badge-role badge-' + role.toLowerCase();
    
    // Show/hide buttons based on role - Only TEACHER has full access now
    const isTeacher = role === 'TEACHER';
    const isStudent = role === 'STUDENT';
    
    // Add buttons visibility
    const addCourseBtn = document.getElementById('addCourseBtn');
    const addDepartmentBtn = document.getElementById('addDepartmentBtn');
    const addStudentBtn = document.getElementById('addStudentBtn');
    const myCoursesNav = document.getElementById('myCoursesNav');
    
    if (addCourseBtn) addCourseBtn.style.display = isTeacher ? 'block' : 'none';
    if (addDepartmentBtn) addDepartmentBtn.style.display = isTeacher ? 'block' : 'none';
    if (addStudentBtn) addStudentBtn.style.display = isTeacher ? 'block' : 'none';
    if (myCoursesNav) myCoursesNav.style.display = isStudent ? 'block' : 'none';
}

// Dashboard Data
async function loadDashboardData() {
    try {
        // Load counts
        const [students, courses, departments] = await Promise.all([
            apiRequest('/api/students').catch(() => ({ data: [] })),
            apiRequest('/api/courses').catch(() => ({ data: [] })),
            apiRequest('/api/departments').catch(() => ({ data: [] }))
        ]);
        
        document.getElementById('studentCount').textContent = students.data?.length || 0;
        document.getElementById('courseCount').textContent = courses.data?.length || 0;
        document.getElementById('departmentCount').textContent = departments.data?.length || 0;
        
        // Try to get teachers count
        try {
            const teachers = await apiRequest('/api/teachers');
            document.getElementById('teacherCount').textContent = teachers.data?.length || 0;
        } catch {
            document.getElementById('teacherCount').textContent = '-';
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

// Students
async function loadStudents() {
    try {
        const isTeacher = currentUser?.role === 'TEACHER';
        // Students can only see their department's students, Teachers can see all
        const endpoint = isTeacher ? '/api/students' : '/api/students/my-department';
        const response = await apiRequest(endpoint);
        const students = response.data || [];
        const tbody = document.getElementById('studentsTable');
        
        if (students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No students found</td></tr>';
            return;
        }
        
        tbody.innerHTML = students.map(student => `
            <tr>
                <td><span class="badge bg-secondary">${student.studentId}</span></td>
                <td><strong>${student.firstName} ${student.lastName}</strong></td>
                <td>${student.email}</td>
                <td>${student.phone || '-'}</td>
                <td>${student.departmentName || '-'}</td>
                <td>${student.enrollmentYear}</td>
                <td>
                    ${isTeacher ? `
                        <button class="btn btn-info btn-action" onclick="viewStudent(${student.id})">
                            <i class="bi bi-eye"></i>
                        </button>
                    ` : '-'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        document.getElementById('studentsTable').innerHTML = 
            `<tr><td colspan="7" class="text-center text-danger">Access denied or error loading students</td></tr>`;
    }
}

async function viewStudent(id) {
    try {
        const response = await apiRequest(`/api/students/${id}`);
        const student = response.data;
        
        const coursesHtml = student.courses?.length > 0 
            ? student.courses.map(c => `<span class="badge bg-primary me-1">${c.code} - ${c.name}</span>`).join('')
            : '<span class="text-muted">No courses enrolled</span>';
        
        document.getElementById('studentDetailsContent').innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <p><strong>Student ID:</strong> ${student.studentId}</p>
                    <p><strong>Name:</strong> ${student.firstName} ${student.lastName}</p>
                    <p><strong>Email:</strong> ${student.email}</p>
                    <p><strong>Phone:</strong> ${student.phone || '-'}</p>
                </div>
                <div class="col-md-6">
                    <p><strong>Address:</strong> ${student.address || '-'}</p>
                    <p><strong>Department:</strong> ${student.departmentName || '-'}</p>
                    <p><strong>Enrollment Year:</strong> ${student.enrollmentYear}</p>
                </div>
            </div>
            <hr>
            <h6>Enrolled Courses:</h6>
            <div>${coursesHtml}</div>
        `;
        new bootstrap.Modal(document.getElementById('viewStudentModal')).show();
    } catch (error) {
        showToast('Error loading student details', 'danger');
    }
}

// Teachers
async function loadTeachers() {
    try {
        const response = await apiRequest('/api/teachers');
        const teachers = response.data || [];
        const tbody = document.getElementById('teachersTable');
        
        if (teachers.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No teachers found</td></tr>';
            return;
        }
        
        const isTeacher = currentUser?.role === 'TEACHER';
        
        tbody.innerHTML = teachers.map(teacher => `
            <tr>
                <td><span class="badge bg-secondary">${teacher.employeeId}</span></td>
                <td><strong>${teacher.firstName} ${teacher.lastName}</strong></td>
                <td>${teacher.email}</td>
                <td>${teacher.phone || '-'}</td>
                <td>${teacher.departmentName || '-'}</td>
                <td>${teacher.specialization || '-'}</td>
                <td>
                    ${isTeacher ? `
                        <button class="btn btn-info btn-action" onclick="viewTeacher(${teacher.id})">
                            <i class="bi bi-eye"></i>
                        </button>
                    ` : '-'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        document.getElementById('teachersTable').innerHTML = 
            `<tr><td colspan="7" class="text-center text-danger">Access denied or error loading teachers</td></tr>`;
    }
}

async function viewTeacher(id) {
    try {
        const response = await apiRequest(`/api/teachers/${id}`);
        const teacher = response.data;
        showToast(`Teacher: ${teacher.firstName} ${teacher.lastName}`, 'info');
    } catch (error) {
        showToast('Error loading teacher details', 'danger');
    }
}

// Courses
async function loadCourses() {
    try {
        const response = await apiRequest('/api/courses');
        const courses = response.data || [];
        const tbody = document.getElementById('coursesTable');
        
        if (courses.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No courses found</td></tr>';
            return;
        }
        
        const isTeacher = currentUser?.role === 'TEACHER';
        
        tbody.innerHTML = courses.map(course => `
            <tr>
                <td><span class="badge bg-primary">${course.code}</span></td>
                <td><strong>${course.name}</strong></td>
                <td>${course.description || '-'}</td>
                <td>${course.credits}</td>
                <td>${course.departmentName || '-'}</td>
                <td>${course.teacherName || '-'}</td>
                <td>
                    <button class="btn btn-info btn-action" onclick="viewCourse(${course.id})">
                        <i class="bi bi-eye"></i>
                    </button>
                    ${isTeacher ? `
                        <button class="btn btn-warning btn-action" onclick="editCourse(${course.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-danger btn-action" onclick="deleteCourse(${course.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    ` : ''}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        document.getElementById('coursesTable').innerHTML = 
            `<tr><td colspan="7" class="text-center text-danger">Error loading courses</td></tr>`;
    }
}

async function viewCourse(id) {
    try {
        const response = await apiRequest(`/api/courses/${id}`);
        const course = response.data;
        
        document.getElementById('courseDetailsContent').innerHTML = `
            <p><strong>Code:</strong> ${course.code}</p>
            <p><strong>Name:</strong> ${course.name}</p>
            <p><strong>Description:</strong> ${course.description || '-'}</p>
            <p><strong>Credits:</strong> ${course.credits}</p>
            <p><strong>Department:</strong> ${course.departmentName || '-'}</p>
            <p><strong>Teacher:</strong> ${course.teacherName || '-'}</p>
        `;
        new bootstrap.Modal(document.getElementById('viewCourseModal')).show();
    } catch (error) {
        showToast('Error loading course details', 'danger');
    }
}

async function openCourseModal(course = null) {
    document.getElementById('courseModalTitle').textContent = course ? 'Edit Course' : 'Add Course';
    document.getElementById('courseForm').reset();
    document.getElementById('courseId').value = course?.id || '';
    
    if (course) {
        document.getElementById('courseCode').value = course.code;
        document.getElementById('courseName').value = course.name;
        document.getElementById('courseDescription').value = course.description || '';
        document.getElementById('courseCredits').value = course.credits;
    }
    
    // Load departments for dropdown
    try {
        const response = await apiRequest('/api/departments');
        const select = document.getElementById('courseDepartment');
        select.innerHTML = '<option value="">Select Department</option>' +
            response.data.map(d => `<option value="${d.id}" ${course?.departmentId === d.id ? 'selected' : ''}>${d.name}</option>`).join('');
    } catch (error) {
        console.error('Error loading departments:', error);
    }
    
    new bootstrap.Modal(document.getElementById('courseModal')).show();
}

async function editCourse(id) {
    try {
        const response = await apiRequest(`/api/courses/${id}`);
        openCourseModal(response.data);
    } catch (error) {
        showToast('Error loading course', 'danger');
    }
}

async function saveCourse(e) {
    e.preventDefault();
    
    const id = document.getElementById('courseId').value;
    const courseData = {
        code: document.getElementById('courseCode').value,
        name: document.getElementById('courseName').value,
        description: document.getElementById('courseDescription').value,
        credits: parseInt(document.getElementById('courseCredits').value),
        departmentId: parseInt(document.getElementById('courseDepartment').value)
    };
    
    try {
        if (id) {
            await apiRequest(`/api/courses/${id}`, 'PUT', courseData);
            showToast('Course updated successfully', 'success');
        } else {
            await apiRequest('/api/courses', 'POST', courseData);
            showToast('Course created successfully', 'success');
        }
        bootstrap.Modal.getInstance(document.getElementById('courseModal')).hide();
        loadCourses();
        loadDashboardData();
    } catch (error) {
        showToast('Error saving course: ' + error.message, 'danger');
    }
}

async function deleteCourse(id) {
    if (confirm('Are you sure you want to delete this course?')) {
        try {
            await apiRequest(`/api/courses/${id}`, 'DELETE');
            showToast('Course deleted successfully', 'success');
            loadCourses();
            loadDashboardData();
        } catch (error) {
            showToast('Error deleting course: ' + error.message, 'danger');
        }
    }
}

// Departments
async function loadDepartments() {
    try {
        const response = await apiRequest('/api/departments');
        const departments = response.data || [];
        const tbody = document.getElementById('departmentsTable');
        
        if (departments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No departments found</td></tr>';
            return;
        }
        
        const isTeacher = currentUser?.role === 'TEACHER';
        
        tbody.innerHTML = departments.map(dept => `
            <tr>
                <td><span class="badge bg-secondary">${dept.code}</span></td>
                <td><strong>${dept.name}</strong></td>
                <td>${dept.description || '-'}</td>
                <td>
                    ${isTeacher ? `
                        <button class="btn btn-warning btn-action" onclick="editDepartment(${dept.id})">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-danger btn-action" onclick="deleteDepartment(${dept.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    ` : '-'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        document.getElementById('departmentsTable').innerHTML = 
            `<tr><td colspan="4" class="text-center text-danger">Error loading departments</td></tr>`;
    }
}

async function openDepartmentModal(dept = null) {
    document.getElementById('departmentModalTitle').textContent = dept ? 'Edit Department' : 'Add Department';
    document.getElementById('departmentForm').reset();
    document.getElementById('departmentId').value = dept?.id || '';
    
    if (dept) {
        document.getElementById('departmentCode').value = dept.code;
        document.getElementById('departmentName').value = dept.name;
        document.getElementById('departmentDescription').value = dept.description || '';
    }
    
    new bootstrap.Modal(document.getElementById('departmentModal')).show();
}

async function editDepartment(id) {
    try {
        const response = await apiRequest(`/api/departments/${id}`);
        openDepartmentModal(response.data);
    } catch (error) {
        showToast('Error loading department', 'danger');
    }
}

async function saveDepartment(e) {
    e.preventDefault();
    
    const id = document.getElementById('departmentId').value;
    const deptData = {
        code: document.getElementById('departmentCode').value,
        name: document.getElementById('departmentName').value,
        description: document.getElementById('departmentDescription').value
    };
    
    try {
        if (id) {
            await apiRequest(`/api/departments/${id}`, 'PUT', deptData);
            showToast('Department updated successfully', 'success');
        } else {
            await apiRequest('/api/departments', 'POST', deptData);
            showToast('Department created successfully', 'success');
        }
        bootstrap.Modal.getInstance(document.getElementById('departmentModal')).hide();
        loadDepartments();
        loadDashboardData();
    } catch (error) {
        showToast('Error saving department: ' + error.message, 'danger');
    }
}

async function deleteDepartment(id) {
    if (confirm('Are you sure you want to delete this department?')) {
        try {
            await apiRequest(`/api/departments/${id}`, 'DELETE');
            showToast('Department deleted successfully', 'success');
            loadDepartments();
            loadDashboardData();
        } catch (error) {
            showToast('Error deleting department: ' + error.message, 'danger');
        }
    }
}

// Navigation
function switchSection(sectionName) {
    // Update nav links
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        link.classList.toggle('active', link.dataset.section === sectionName);
    });
    
    // Update sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.toggle('active', section.id === sectionName + 'Content');
    });
    
    // Update page title
    document.getElementById('pageTitle').textContent = 
        sectionName.charAt(0).toUpperCase() + sectionName.slice(1);
    
    // Load data for section
    switch (sectionName) {
        case 'students':
            loadStudents();
            break;
        case 'teachers':
            loadTeachers();
            break;
        case 'courses':
            loadCourses();
            break;
        case 'departments':
            loadDepartments();
            break;
        case 'dashboard':
            loadDashboardData();
            break;
        case 'myCourses':
            loadMyCourses();
            break;
    }
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Login form
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;
        login(email, password);
    });
    
    // Navigation
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            switchSection(this.dataset.section);
        });
    });
    
    // Forms
    document.getElementById('courseForm').addEventListener('submit', saveCourse);
    document.getElementById('departmentForm').addEventListener('submit', saveDepartment);
    document.getElementById('studentForm').addEventListener('submit', saveStudent);
    
    // Check for existing session
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');
    
    if (savedToken && savedUser) {
        authToken = savedToken;
        currentUser = JSON.parse(savedUser);
        showDashboard();
        loadDashboardData();
    } else {
        showLogin();
    }
});

// ============ Student CRUD Functions ============

async function openStudentModal(student = null) {
    document.getElementById('studentModalTitle').textContent = student ? 'Edit Student' : 'Add Student';
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = student?.id || '';
    
    // Show/hide password field (only for new students)
    const passwordContainer = document.getElementById('passwordFieldContainer');
    const passwordInput = document.getElementById('studentPassword');
    if (student) {
        passwordContainer.style.display = 'none';
        passwordInput.removeAttribute('required');
    } else {
        passwordContainer.style.display = 'block';
        passwordInput.setAttribute('required', 'required');
    }
    
    if (student) {
        document.getElementById('studentFirstName').value = student.firstName || '';
        document.getElementById('studentLastName').value = student.lastName || '';
        document.getElementById('studentEmail').value = student.email || '';
        document.getElementById('studentPhone').value = student.phone || '';
        document.getElementById('studentEnrollmentYear').value = student.enrollmentYear || new Date().getFullYear();
        document.getElementById('studentAddress').value = student.address || '';
    }
    
    // Load departments for dropdown
    try {
        const response = await apiRequest('/api/departments');
        const select = document.getElementById('studentDepartment');
        select.innerHTML = '<option value="">Select Department</option>' +
            response.data.map(d => `<option value="${d.id}" ${student?.departmentId === d.id ? 'selected' : ''}>${d.name}</option>`).join('');
    } catch (error) {
        console.error('Error loading departments:', error);
    }
    
    new bootstrap.Modal(document.getElementById('studentModal')).show();
}

async function saveStudent(e) {
    e.preventDefault();
    
    const id = document.getElementById('studentId').value;
    
    try {
        if (id) {
            // Update existing student
            const updateData = {
                firstName: document.getElementById('studentFirstName').value,
                lastName: document.getElementById('studentLastName').value,
                phone: document.getElementById('studentPhone').value,
                address: document.getElementById('studentAddress').value
            };
            await apiRequest(`/api/students/${id}`, 'PUT', updateData);
            showToast('Student updated successfully', 'success');
        } else {
            // Create new student via register endpoint
            const studentData = {
                firstName: document.getElementById('studentFirstName').value,
                lastName: document.getElementById('studentLastName').value,
                email: document.getElementById('studentEmail').value,
                password: document.getElementById('studentPassword').value,
                role: 'STUDENT',
                phone: document.getElementById('studentPhone').value,
                address: document.getElementById('studentAddress').value,
                departmentId: parseInt(document.getElementById('studentDepartment').value),
                enrollmentYear: parseInt(document.getElementById('studentEnrollmentYear').value) || new Date().getFullYear()
            };
            await apiRequest('/api/auth/register', 'POST', studentData);
            showToast('Student created successfully', 'success');
        }
        bootstrap.Modal.getInstance(document.getElementById('studentModal')).hide();
        loadStudents();
        loadDashboardData();
    } catch (error) {
        showToast('Error saving student: ' + error.message, 'danger');
    }
}

// ============ My Courses Functions (for Students) ============

async function loadMyCourses() {
    const myCoursesContainer = document.getElementById('myCoursesContainer');
    const availableCoursesContainer = document.getElementById('availableCoursesContainer');
    
    try {
        // Get current student info with enrolled courses
        const studentResponse = await apiRequest('/api/students/me');
        const student = studentResponse.data;
        const enrolledCourses = student.courses || [];
        
        // Display enrolled courses
        if (enrolledCourses.length === 0) {
            myCoursesContainer.innerHTML = '<p class="text-muted">You are not enrolled in any courses yet.</p>';
        } else {
            myCoursesContainer.innerHTML = `
                <div class="row">
                    ${enrolledCourses.map(course => `
                        <div class="col-md-6 col-lg-4 mb-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h6 class="card-title"><span class="badge bg-primary me-2">${course.code}</span>${course.name}</h6>
                                    <p class="card-text small text-muted">${course.description || 'No description'}</p>
                                    <p class="card-text"><small><strong>Credits:</strong> ${course.credits}</small></p>
                                </div>
                                <div class="card-footer bg-transparent">
                                    <button class="btn btn-outline-danger btn-sm w-100" onclick="dropMyCourse(${course.id})">
                                        <i class="bi bi-x-circle me-1"></i>Drop Course
                                    </button>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
        }
        
        // Get all courses and filter out enrolled ones
        const allCoursesResponse = await apiRequest('/api/courses');
        const allCourses = allCoursesResponse.data || [];
        const enrolledIds = enrolledCourses.map(c => c.id);
        const availableCourses = allCourses.filter(c => !enrolledIds.includes(c.id));
        
        if (availableCourses.length === 0) {
            availableCoursesContainer.innerHTML = '<p class="text-muted">No more courses available to enroll.</p>';
        } else {
            availableCoursesContainer.innerHTML = `
                <div class="row">
                    ${availableCourses.map(course => `
                        <div class="col-md-6 col-lg-4 mb-3">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h6 class="card-title"><span class="badge bg-secondary me-2">${course.code}</span>${course.name}</h6>
                                    <p class="card-text small text-muted">${course.description || 'No description'}</p>
                                    <p class="card-text"><small><strong>Credits:</strong> ${course.credits} | <strong>Teacher:</strong> ${course.teacherName || 'TBA'}</small></p>
                                </div>
                                <div class="card-footer bg-transparent">
                                    <button class="btn btn-primary btn-sm w-100" onclick="enrollInCourse(${course.id})">
                                        <i class="bi bi-plus-circle me-1"></i>Enroll
                                    </button>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
        }
    } catch (error) {
        myCoursesContainer.innerHTML = '<p class="text-danger">Error loading your courses.</p>';
        availableCoursesContainer.innerHTML = '<p class="text-danger">Error loading available courses.</p>';
    }
}

async function enrollInCourse(courseId) {
    try {
        await apiRequest(`/api/students/me/courses/${courseId}`, 'POST');
        showToast('Successfully enrolled in course!', 'success');
        loadMyCourses();
    } catch (error) {
        showToast('Error enrolling in course: ' + error.message, 'danger');
    }
}

async function dropMyCourse(courseId) {
    if (confirm('Are you sure you want to drop this course?')) {
        try {
            await apiRequest(`/api/students/me/courses/${courseId}`, 'DELETE');
            showToast('Successfully dropped from course', 'success');
            loadMyCourses();
        } catch (error) {
            showToast('Error dropping course: ' + error.message, 'danger');
        }
    }
}
