import { useState, useEffect } from 'react';
import axios from '../api/Axios';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

function Dashboard() {
  const [longUrl, setLongUrl] = useState('');
  const [shortened, setShortened] = useState('');
  const [history, setHistory] = useState([]);
  const navigate = useNavigate();

  const token = localStorage.getItem('accessToken');

  useEffect(() => {
    if (!token) {
      navigate('/login');
    } else {
      fetchHistory();
    }
  }, []);

  const fetchHistory = async () => {
    try {
      const res = await axios.get('/users/history');
      console.log(res.data);
      setHistory(res.data);
    } catch (err) {
      console.error('Failed to fetch history', err);
    }
  };

  const handleShorten = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('/url/saveUrl', { originalUrl: longUrl });
      setShortened(res.data.shortUrl);
      fetchHistory();
    } catch (err) {
      alert('Shortening failed');
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    navigate('/login');
  };

  return (
    <div className="dashboard-container">
      <h2>Dashboard</h2>

      <form onSubmit={handleShorten}>
        <input
          type="text"
          placeholder="Paste long URL"
          value={longUrl}
          onChange={(e) => setLongUrl(e.target.value)}
        />
        <button type="submit">Shorten</button>
      </form>

      {shortened && <p className="shortened-url">Shortened URL: <a href={shortened} target="_blank" rel="noreferrer">{shortened}</a></p>}

      <h3>Your History</h3>
      <ul className="history-list">
        {history.map((item, i) => (
          <li key={i}>
            <strong>{item.originalUrl}</strong> → <a href={item.shortUrl} target="_blank" rel="noreferrer">{item.shortUrl}</a>
          </li>
        ))}
      </ul>

      <button onClick={logout} className="logout-btn">Logout</button>
    </div>
  );
}

export default Dashboard;
