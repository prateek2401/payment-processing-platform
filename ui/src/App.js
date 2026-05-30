import React, { useState, useEffect } from 'react';
import axios from 'axios';

const App = () => {
    const [formData, setFormData] = useState({
        userId: 'user-101',
        amount: '99.99',
        currency: 'USD'
    });
    const [payments, setPayments] = useState([]);
    const [analytics, setAnalytics] = useState([]);
    const [userAnalytics, setUserAnalytics] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await axios.post('/api/producer/payments', formData);
            setFormData({ userId: 'user-101', amount: '99.99', currency: 'USD' });
            fetchData();
        } catch (error) {
            console.error('Error sending payment:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchData = async () => {
        try {
            console.log('Fetching payments from /api/processor/payments');
            const paymentsRes = await axios.get('/api/processor/payments');
            console.log('Payments response:', paymentsRes.data);

            console.log('Fetching analytics from /api/analytics-service/analytics');
            const analyticsRes = await axios.get('/api/analytics-service/analytics');
            console.log('Analytics response:', analyticsRes.data);

            console.log('Fetching user analytics from /api/analytics-service/analytics/users');
            const userAnalyticsRes = await axios.get('/api/analytics-service/analytics/users');
            console.log('User analytics response:', userAnalyticsRes.data);

            setPayments(paymentsRes.data);
            setAnalytics(analyticsRes.data);
            setUserAnalytics(userAnalyticsRes.data);
        } catch (error) {
            console.error('Error fetching data:', error);
            console.error('Error details:', error.response?.data, error.response?.status, error.response?.headers);
        }
    };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, []);

  const styles = {
    container: { maxWidth: '1400px', margin: '0 auto' },
    header: {
      textAlign: 'center', color: '#fff', padding: '30px 0', marginBottom: '30px'
    },
    title: { fontSize: '2.5rem', fontWeight: 700, marginBottom: '10px', textShadow: '2px 2px 4px rgba(0,0,0,0.2)' },
    subtitle: { fontSize: '1.1rem', opacity: 0.9 },
    grid: { display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '20px', marginBottom: '20px' },
    card: {
      background: '#fff', borderRadius: '16px', padding: '25px',
      boxShadow: '0 10px 40px rgba(0,0,0,0.15)'
    },
    cardTitle: { fontSize: '1.3rem', color: '#2d3748', marginBottom: '20px', fontWeight: 600 },
    form: { display: 'flex', flexDirection: 'column', gap: '15px' },
    formGroup: { display: 'flex', flexDirection: 'column', gap: '6px' },
    label: { color: '#4a5568', fontSize: '0.9rem', fontWeight: 500 },
    input: {
      padding: '12px 16px', border: '2px solid #e2e8f0', borderRadius: '10px',
      fontSize: '1rem', transition: 'border-color 0.2s'
    },
    select: {
      padding: '12px 16px', border: '2px solid #e2e8f0', borderRadius: '10px',
      fontSize: '1rem', background: '#fff', cursor: 'pointer'
    },
    button: {
      padding: '14px 24px', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: '#fff', border: 'none', borderRadius: '10px', fontSize: '1rem',
      fontWeight: 600, cursor: 'pointer', transition: 'transform 0.2s, box-shadow 0.2s'
    },
    buttonHover: { transform: 'translateY(-2px)', boxShadow: '0 6px 20px rgba(102, 126, 234, 0.4)' },
    tablesGrid: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' },
    table: { width: '100%', borderCollapse: 'collapse', marginTop: '10px' },
    th: {
      background: '#f7fafc', padding: '12px 10px', textAlign: 'left',
      color: '#4a5568', fontWeight: 600, fontSize: '0.85rem', borderBottom: '2px solid #e2e8f0'
    },
    td: { padding: '12px 10px', color: '#2d3748', fontSize: '0.9rem', borderBottom: '1px solid #f0f0f0' },
    statusBadge: {
      display: 'inline-block', padding: '4px 12px', borderRadius: '20px',
      fontSize: '0.8rem', fontWeight: 600, background: '#c6f6d5', color: '#22543d'
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1 style={styles.title}>💳 Payment Processing Platform</h1>
        <p style={styles.subtitle}>Event-driven system with Kafka, PostgreSQL & Redis</p>
      </div>

      <div style={styles.grid}>
        <div style={styles.card}>
          <h2 style={styles.cardTitle}>📤 Send New Payment</h2>
          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.formGroup}>
              <label style={styles.label}>User ID</label>
              <select
                style={styles.select}
                value={formData.userId}
                onChange={(e) => setFormData({...formData, userId: e.target.value})}
              >
                <option value="user-101">user-101</option>
                <option value="user-102">user-102</option>
                <option value="user-103">user-103</option>
                <option value="user-104">user-104</option>
                <option value="user-105">user-105</option>
              </select>
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Amount</label>
              <input
                style={styles.input}
                type="number"
                step="0.01"
                value={formData.amount}
                onChange={(e) => setFormData({...formData, amount: e.target.value})}
              />
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Currency</label>
              <select
                style={styles.select}
                value={formData.currency}
                onChange={(e) => setFormData({...formData, currency: e.target.value})}
              >
                <option value="USD">USD</option>
                <option value="EUR">EUR</option>
                <option value="INR">INR</option>
                <option value="GBP">GBP</option>
              </select>
            </div>
            <button
              type="submit"
              style={styles.button}
              disabled={loading}
              onMouseEnter={(e) => e.currentTarget.style.cssText = `
                ${Object.entries({...styles.button, ...styles.buttonHover}).map(([k,v]) => `${k}:${v}`).join(';')}
              `}
              onMouseLeave={(e) => e.currentTarget.style.cssText = `
                ${Object.entries(styles.button).map(([k,v]) => `${k}:${v}`).join(';')}
              `}
            >
              {loading ? 'Sending...' : 'Send Payment'}
            </button>
          </form>
        </div>

        <div style={styles.card}>
          <h2 style={styles.cardTitle}>📊 Live Analytics</h2>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Date</th>
                <th style={styles.th}>Currency</th>
                <th style={styles.th}>Transactions</th>
                <th style={styles.th}>Total Amount</th>
              </tr>
            </thead>
            <tbody>
              {analytics.map((a, i) => (
                <tr key={i}>
                  <td style={styles.td}>{a.date}</td>
                  <td style={styles.td}>{a.currency}</td>
                  <td style={styles.td}>{a.totalTransactions}</td>
                  <td style={styles.td}>{a.totalAmount.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div style={styles.card}>
          <h2 style={styles.cardTitle}>👤 User Analytics</h2>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Date</th>
                <th style={styles.th}>User ID</th>
                <th style={styles.th}>Currency</th>
                <th style={styles.th}>Transactions</th>
                <th style={styles.th}>Total Amount</th>
              </tr>
            </thead>
            <tbody>
              {userAnalytics.map((a, i) => (
                <tr key={i}>
                  <td style={styles.td}>{a.date}</td>
                  <td style={styles.td}>{a.userId}</td>
                  <td style={styles.td}>{a.currency}</td>
                  <td style={styles.td}>{a.totalTransactions}</td>
                  <td style={styles.td}>{a.totalAmount.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div style={styles.card}>
        <h2 style={styles.cardTitle}>💰 Processed Payments</h2>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>Payment ID</th>
              <th style={styles.th}>User ID</th>
              <th style={styles.th}>Amount</th>
              <th style={styles.th}>Status</th>
              <th style={styles.th}>Partition</th>
              <th style={styles.th}>Offset</th>
            </tr>
          </thead>
          <tbody>
            {payments.map((p) => (
              <tr key={p.id}>
                <td style={styles.td}>{p.paymentId}</td>
                <td style={styles.td}>{p.userId}</td>
                <td style={styles.td}>{p.amount.toFixed(2)} {p.currency}</td>
                <td style={styles.td}><span style={styles.statusBadge}>{p.status}</span></td>
                <td style={styles.td}>{p.partition}</td>
                <td style={styles.td}>{p.offset}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default App;
