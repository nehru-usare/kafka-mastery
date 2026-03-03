import React, { useState, useEffect } from 'react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { Package, DollarSign, AlertTriangle, Activity, Zap } from 'lucide-react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './Dashboard.css';

const Dashboard = () => {
    const [orders, setOrders] = useState([]);
    const [metrics, setMetrics] = useState({ totalOrders: 0, revenue: 0, failed: 0 });
    const [chartData, setChartData] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        // 1. Initialize WebSocket Connection via STOMP over SockJS
        const stompClient = new Client({
            // Provide SockJS instance pointing to the Notification Service websocket URL
            webSocketFactory: () => new SockJS('http://localhost:8083/ws-notification'),
            connectHeaders: {
                // notification-service requires basic auth (admin/admin123)
                Authorization: 'Basic ' + window.btoa('admin:admin123')
            },
            debug: function (str) {
                console.log(str);
            },
            // Retry connection every 5s if backend goes down
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('✅ Connected to WebSocket backend!');

                // 2. Subscribe to the destination the backend sends messages to
                stompClient.subscribe('/topic/notifications', (message) => {
                    if (message.body) {
                        const event = JSON.parse(message.body);
                        console.log('🔵 Received Kafka Event:', event);

                        const newOrder = {
                            id: event.orderId,
                            amount: event.amount,
                            status: event.status,
                            time: new Date().toLocaleTimeString()
                        };

                        // Update Orders List (keep newest 8)
                        setOrders(prev => [newOrder, ...prev].slice(0, 8));

                        // Update Metrics (Aggregated locally based on stream)
                        if (newOrder.status === 'SUCCESS') {
                            setMetrics(prev => ({
                                ...prev,
                                totalOrders: prev.totalOrders + 1,
                                revenue: prev.revenue + newOrder.amount
                            }));

                            setChartData(prev => {
                                // Keep chart array max size 15 points
                                const baseData = prev.length >= 15 ? prev.slice(1) : prev;
                                return [...baseData, {
                                    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                                    revenue: newOrder.amount
                                }];
                            });
                        } else {
                            setMetrics(prev => ({
                                ...prev,
                                failed: prev.failed + 1
                            }));
                        }
                    }
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        stompClient.activate();

        // Clean up on component unmount
        return () => {
            stompClient.deactivate();
        };
    }, []);

    const handlePlaceOrder = async () => {
        setIsLoading(true);

        try {
            const isSuccess = Math.random() > 0.15;
            const amount = Math.floor(10 + Math.random() * 500);

            const response = await fetch('http://localhost:8081/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    customerId: `CUST-${Math.floor(100 + Math.random() * 900)}`,
                    amount: amount,
                    status: isSuccess ? 'SUCCESS' : 'FAILED'
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log("Successfully posted new order to the backend");
            // Note: We DO NOT manually update the state here anymore!
            // The state will be auto-updated when the Kafka Consumer finishes processing
            // and pushes the message out over the WebSocket!

        } catch (error) {
            console.error("Failed to place order:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const totalProcessed = metrics.totalOrders + metrics.failed;
    const successRate = totalProcessed === 0 ? "100.0" : ((metrics.totalOrders / totalProcessed) * 100).toFixed(1);

    return (
        <div className="dashboard-grid">

            {/* Metrics Row */}
            <div className="metrics-container">
                <div className="metric-card" style={{ animationDelay: '0.1s' }}>
                    <div className="metric-icon">
                        <Package size={28} color="var(--accent-primary)" />
                    </div>
                    <div className="metric-content">
                        <h3>Total Orders</h3>
                        <p className="metric-value">{metrics.totalOrders}</p>
                    </div>
                </div>

                <div className="metric-card" style={{ animationDelay: '0.2s' }}>
                    <div className="metric-icon revenue-icon">
                        <DollarSign size={28} color="var(--accent-success)" />
                    </div>
                    <div className="metric-content">
                        <h3>Total Revenue</h3>
                        <p className="metric-value">${metrics.revenue.toLocaleString()}</p>
                    </div>
                </div>

                <div className="metric-card" style={{ animationDelay: '0.3s' }}>
                    <div className="metric-icon failed-icon">
                        <AlertTriangle size={28} color="var(--accent-error)" />
                    </div>
                    <div className="metric-content">
                        <h3>Failed Events</h3>
                        <div className="metric-split">
                            <p className="metric-value warning">{metrics.failed}</p>
                            <span className="success-rate-badge">{successRate}% Success</span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="dashboard-main">
                {/* Left Column: Actions & Charts */}
                <div className="left-panel-group">
                    <div className="panel action-panel">
                        <div className="panel-header">
                            <h2><Zap size={20} className="header-icon" /> Emulate Traffic</h2>
                        </div>
                        <p>Generate a real Order request that writes to the outbox and publishes dynamically to Kafka.</p>

                        <button
                            className={`btn-primary ${isLoading ? 'loading' : ''}`}
                            onClick={handlePlaceOrder}
                            disabled={isLoading}
                        >
                            {isLoading ? 'Processing...' : 'Place Order via HTTP'}
                            <div className="btn-glow"></div>
                        </button>
                    </div>

                    <div className="panel chart-panel">
                        <div className="panel-header">
                            <h2><Activity size={20} className="header-icon" /> Revenue Overview</h2>
                        </div>
                        <div className="chart-container">
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                                    <defs>
                                        <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="var(--accent-primary)" stopOpacity={0.4} />
                                            <stop offset="95%" stopColor="var(--accent-primary)" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                                    <XAxis dataKey="time" stroke="var(--text-secondary)" fontSize={12} tickLine={false} axisLine={false} />
                                    <YAxis stroke="var(--text-secondary)" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `$${val}`} />
                                    <Tooltip
                                        contentStyle={{ backgroundColor: 'rgba(15, 23, 42, 0.9)', border: '1px solid var(--glass-border)', borderRadius: '8px', color: '#fff' }}
                                        itemStyle={{ color: 'var(--accent-primary)' }}
                                    />
                                    <Area type="monotone" dataKey="revenue" stroke="var(--accent-primary)" strokeWidth={3} fillOpacity={1} fill="url(#colorRevenue)" />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </div>

                {/* Right Column: Live Stream */}
                <div className="panel stream-panel">
                    <div className="panel-header">
                        <h2>Live Event Stream</h2>
                        <span className="live-badge">LIVE 🔴</span>
                    </div>

                    <div className="events-list">
                        {orders.length === 0 && <p style={{ color: 'var(--text-secondary)', textAlign: 'center', marginTop: '2rem' }}>Awaiting events from WebSocket...</p>}
                        {orders.map((order, index) => (
                            <div key={order.id + '-' + index} className="event-item" style={{ animationDelay: `${index * 0.05}s` }}>
                                <div className="event-details">
                                    <span className="event-id">{order.id}</span>
                                    <span className="event-time">{order.time}</span>
                                </div>
                                <div className="event-amount">${parseFloat(order.amount).toFixed(2)}</div>
                                <div className={`event-status ${order.status.toLowerCase()}`}>
                                    {order.status === 'SUCCESS' ? 'Processed' : 'DLQ Routed'}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

        </div>
    );
};

export default Dashboard;
