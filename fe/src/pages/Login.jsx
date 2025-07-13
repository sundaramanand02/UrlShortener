// src/pages/Login.js
import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './Login.css'


function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(
        'http://localhost:8080/users/login',
        { username, password },
        { withCredentials: true } // ⬅️ send cookie (refresh token)
      );
      localStorage.setItem('accessToken', res.data.accessToken); // Save access token
      navigate('/dashboard'); // we'll create this page later
    } catch (err) {
      alert('Login failed');
    }
  };
return (
  <div className="login-container">
    <h2>Login</h2>
    <form onSubmit={handleLogin}>
      <input
        type="text"
        value={username}
        placeholder="Username"
        onChange={(e) => setUsername(e.target.value)}
      /><br />
      <input
        type="password"
        value={password}
        placeholder="Password"
        onChange={(e) => setPassword(e.target.value)}
      /><br />
      <button type="submit">Login</button>
    </form>
    <p>
  Don't have an account? <a href="/register">Register</a>
</p>
  </div>
);
}

export default Login;
