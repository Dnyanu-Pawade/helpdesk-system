if (!localStorage.getItem('token')) window.location.href = '/index.html';
const currentUser = api.getUser();
document.addEventListener('DOMContentLoaded', () => {
  const unEl = document.getElementById('sidebar-username');
  const roleEl = document.getElementById('sidebar-role');
  if (unEl && currentUser) unEl.textContent = currentUser.fullName || currentUser.username;
  if (roleEl && currentUser) roleEl.textContent = (currentUser.role||'').replace('ROLE_','').replace('_',' ');
  api.getProfile().then(p => {
    const avatarEl = document.querySelector('.user-avatar');
    if (avatarEl && p && p.profilePhoto)
      avatarEl.innerHTML = `<img src="${p.profilePhoto}" style="width:36px;height:36px;border-radius:50%;object-fit:cover"/>`;
  }).catch(()=>{});
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) logoutBtn.addEventListener('click', () => { localStorage.clear(); window.location.href = '/index.html'; });
  api.getUnreadCount().then(d => {
    const badge = document.getElementById('notif-badge');
    if (badge && d && d.count > 0) { badge.textContent = d.count; badge.classList.remove('d-none'); }
  }).catch(()=>{});
});
function formatDate(d) { if (!d) return '-'; return new Date(d).toLocaleDateString('en-IN'); }
function formatDateTime(d) { if (!d) return '-'; return new Date(d).toLocaleString('en-IN'); }
function priorityBadge(p) {
  const map = {LOW:'success',MEDIUM:'warning',HIGH:'danger',CRITICAL:'dark'};
  return `<span class="badge bg-${map[p]||'secondary'}">${p}</span>`;
}
function statusBadge(s) {
  const map = {OPEN:'primary',ASSIGNED:'info',IN_PROGRESS:'warning',WAITING_FOR_USER:'secondary',RESOLVED:'success',CLOSED:'dark',REJECTED:'danger'};
  return `<span class="badge bg-${map[s]||'secondary'}">${(s||'').replace(/_/g,' ')}</span>`;
}
function showToast(msg, type='success') {
  const t = document.createElement('div');
  t.className = `toast-msg toast-${type}`; t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}
