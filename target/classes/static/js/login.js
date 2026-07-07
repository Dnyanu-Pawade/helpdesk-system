document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = document.getElementById('login-btn');
  const errEl = document.getElementById('login-error');
  errEl.classList.add('d-none');
  btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Signing in...';
  try {
    const data = await api.login(document.getElementById('username').value, document.getElementById('password').value);
    if (data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('refreshToken', data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data));
      const role = data.role;
      if (role === 'ROLE_ADMIN') window.location.href = '/html/admin-dashboard.html';
      else if (role === 'ROLE_TEAM_LEAD') window.location.href = '/html/teamlead-dashboard.html';
      else if (role === 'ROLE_SUPPORT_ENGINEER') window.location.href = '/html/engineer-dashboard.html';
      else window.location.href = '/html/employee-dashboard.html';
    } else {
      errEl.textContent = data.message || 'Invalid credentials'; errEl.classList.remove('d-none');
    }
  } catch(err) { errEl.textContent = err.message; errEl.classList.remove('d-none'); }
  finally { btn.disabled = false; btn.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>Sign In'; }
});
