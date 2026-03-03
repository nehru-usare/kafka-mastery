import React, { useState } from 'react';
import { Lock, User } from 'lucide-react';
import './Login.css';

const Login = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        // Simulate authentication check
        setTimeout(() => {
            // Use the credentials that match Spring Security config (admin/admin123)
            if (username === 'admin' && password === 'admin123') {
                onLogin(true);
            } else {
                setError('Invalid username or password');
                setIsLoading(false);
            }
        }, 800);
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <div className="login-icon">
                        <Lock size={32} color="var(--accent-primary)" />
                    </div>
                    <h2>Welcome Back</h2>
                    <p>Sign in to access the event dashboard</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="input-group">
                        <User size={20} className="input-icon" />
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <Lock size={20} className="input-icon" />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <button
                        type="submit"
                        className={`btn-primary login-btn ${isLoading ? 'loading' : ''}`}
                        disabled={isLoading}
                    >
                        {isLoading ? 'Authenticating...' : 'Sign In'}
                        <div className="btn-glow"></div>
                    </button>
                </form>

                <div className="login-footer">
                    <p>Demo Credentials: admin / admin123</p>
                </div>
            </div>
        </div>
    );
};

export default Login;
