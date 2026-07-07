const BASE_URL = '';
const api = {
  getToken() { return localStorage.getItem('token'); },
  getUser() { return JSON.parse(localStorage.getItem('user') || 'null'); },
  getRefreshToken() { return localStorage.getItem('refreshToken'); },
  headers() { return { 'Content-Type': 'application/json', 'Authorization': `Bearer ${this.getToken()}` }; },
  async request(method, path, body = null, silent = false) {
    const opts = { method, headers: this.headers() };
    if (body) opts.body = JSON.stringify(body);
    let res;
    try { res = await fetch(BASE_URL + path, opts); } catch(e) { if (!silent) throw new Error('Network error'); return null; }
    if (res.status === 401) {
      if (this.getRefreshToken()) {
        const ok = await this.tryRefresh();
        if (ok) { opts.headers = this.headers(); res = await fetch(BASE_URL + path, opts); }
        else { if (!silent) { localStorage.clear(); window.location.href = '/index.html'; } return null; }
      } else { if (!silent) { localStorage.clear(); window.location.href = '/index.html'; } return null; }
    }
    if (res.status === 403) { if (!silent) throw new Error('Access denied'); return null; }
    if (res.status === 204) return null;
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || 'Request failed');
    return data;
  },
  async tryRefresh() {
    try {
      const res = await fetch('/api/auth/refresh-token', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({refreshToken: this.getRefreshToken()}) });
      if (!res.ok) return false;
      const d = await res.json();
      localStorage.setItem('token', d.token);
      if (d.refreshToken) localStorage.setItem('refreshToken', d.refreshToken);
      return true;
    } catch { return false; }
  },
  get(path, silent=false) { return this.request('GET', path, null, silent); },
  post(path, body) { return this.request('POST', path, body); },
  put(path, body) { return this.request('PUT', path, body); },
  patch(path, body) { return this.request('PATCH', path, body); },
  delete(path) { return this.request('DELETE', path); },
  login(u, p) { return fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username:u,password:p})}).then(r=>r.json()); },
  forgotPassword(email) { return fetch('/api/auth/forgot-password',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email})}).then(r=>r.json()); },
  resetPassword(token,newPassword) { return fetch('/api/auth/reset-password',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({token,newPassword})}).then(r=>r.json()); },
  // Tickets
  createTicket(data) { return this.post('/api/tickets', data); },
  getMyTickets() { return this.get('/api/tickets/my'); },
  searchMyTickets(p) { return this.get(`/api/tickets/my/search?${new URLSearchParams(p)}`); },
  getTicketById(id) { return this.get(`/api/tickets/${id}`); },
  closeTicket(id) { return this.post(`/api/tickets/${id}/close`); },
  reopenTicket(id) { return this.post(`/api/tickets/${id}/reopen`); },
  getComments(id) { return this.get(`/api/tickets/${id}/comments`); },
  addComment(id, content) { return this.post(`/api/tickets/${id}/comments`, {content}); },
  getAttachments(id) { return this.get(`/api/tickets/${id}/attachments`); },
  getHistory(id) { return this.get(`/api/tickets/${id}/history`); },
  // Engineer
  getAssignedTickets() { return this.get('/api/engineer/assigned'); },
  searchAssignedTickets(p) { return this.get(`/api/engineer/assigned/search?${new URLSearchParams(p)}`); },
  updateTicketStatus(id, status, notes) { return this.put(`/api/engineer/tickets/${id}/status`, {status, notes}); },
  resolveTicket(id, resolution) { return this.put(`/api/engineer/tickets/${id}/resolve`, {resolution}); },
  addEngineerComment(id, content, type) { return this.post(`/api/engineer/tickets/${id}/comments`, {content, type}); },
  // Team Lead
  getOpenTickets() { return this.get('/api/team-lead/open'); },
  searchAllTickets(p) { return this.get(`/api/team-lead/all/search?${new URLSearchParams(p)}`); },
  assignTicket(id, engineerId) { return this.post(`/api/team-lead/tickets/${id}/assign`, {engineerId}); },
  getEngineers() { return this.get('/api/team-lead/engineers'); },
  // Admin
  getDashboard() { return this.get('/api/admin/dashboard'); },
  getAllUsers() { return this.get('/api/admin/users'); },
  updateUserRole(id, role) { return this.patch(`/api/admin/users/${id}/role?role=${role}`); },
  toggleUserStatus(id) { return this.patch(`/api/admin/users/${id}/toggle`); },
  getDepartments() { return this.get('/api/admin/departments'); },
  createDepartment(data) { return this.post('/api/admin/departments', data); },
  getAuditLogs(limit=100) { return this.get(`/api/admin/audit-logs?limit=${limit}`, true); },
  getCategoryChart() { return this.get('/api/admin/charts/category', true); },
  getPriorityChart() { return this.get('/api/admin/charts/priority', true); },
  getMonthlyChart() { return this.get('/api/admin/charts/monthly', true); },
  getEngineerPerformance() { return this.get('/api/admin/charts/engineer-performance', true); },
  // Notifications
  getNotifications() { return this.get('/api/notifications'); },
  getUnreadCount() { return this.get('/api/notifications/unread-count', true); },
  markNotificationsRead() { return this.patch('/api/notifications/mark-read'); },
  // Profile
  getProfile() { return this.get('/api/users/me'); },
  updateProfile(data) { return this.put('/api/users/me', data); },
  changePassword(currentPassword, newPassword) { return this.post('/api/users/me/change-password', {currentPassword, newPassword}); },
};
