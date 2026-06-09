import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { Ban, Check, CircleDashed, LogOut, RefreshCcw, Search, ShieldX, Trash2 } from 'lucide-react';
import './styles.css';

type ApiResponse<T> = {
  code: number;
  message: string;
  data: T;
};

type CourtStatus = 'pending' | 'approved' | 'voided' | 'blacklisted' | 'all';

type Court = {
  id: string;
  name: string;
  address?: string;
  country?: string;
  city?: string;
  approvalStatus: CourtStatus;
  approvalStatusText: string;
  venueStatus?: string;
  submitterId?: string;
  submitterName?: string;
  submitterEmail?: string;
  submitterPhone?: string;
  photoUrls: string[];
  wechatMiniProgramName?: string;
  description?: string;
  reviewedBy?: string;
  reviewedAt?: string;
  rejectedReason?: string;
};

const ADMIN_EMAIL = '656619107@qq.com';

const statusTabs: Array<{ value: CourtStatus; label: string; icon: React.ReactNode }> = [
  { value: 'pending', label: '审核中', icon: <CircleDashed size={16} /> },
  { value: 'approved', label: '通过', icon: <Check size={16} /> },
  { value: 'voided', label: '作废', icon: <Trash2 size={16} /> },
  { value: 'blacklisted', label: '黑名单', icon: <ShieldX size={16} /> },
  { value: 'all', label: '全部', icon: <Search size={16} /> }
];

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('tennerAdminToken') || '');
  const [email, setEmail] = useState(ADMIN_EMAIL);
  const [password, setPassword] = useState('');
  const [status, setStatus] = useState<CourtStatus>('pending');
  const [courts, setCourts] = useState<Court[]>([]);
  const [selectedId, setSelectedId] = useState('');
  const [query, setQuery] = useState('');
  const [reason, setReason] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const selectedCourt = courts.find((court) => court.id === selectedId) || courts[0];

  const visibleCourts = useMemo(() => {
    const keyword = query.trim().toLowerCase();
    if (!keyword) {
      return courts;
    }
    return courts.filter((court) => {
      return [court.name, court.address, court.submitterName, court.submitterEmail, court.wechatMiniProgramName]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(keyword));
    });
  }, [courts, query]);

  useEffect(() => {
    if (token) {
      void loadCourts(status);
    }
  }, [token, status]);

  async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers
      }
    });
    const body = (await response.json()) as ApiResponse<T>;
    if (!response.ok || body.code !== 200) {
      throw new Error(body.message || '请求失败');
    }
    return body.data;
  }

  async function login(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setMessage('');
    try {
      const data = await request<{ token: string }>('/api/admin/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      });
      localStorage.setItem('tennerAdminToken', data.token);
      setToken(data.token);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '登录失败');
    } finally {
      setLoading(false);
    }
  }

  async function loadCourts(nextStatus: CourtStatus) {
    setLoading(true);
    setMessage('');
    try {
      const data = await request<Court[]>(`/api/admin/courts?status=${nextStatus}`);
      setCourts(data);
      setSelectedId(data[0]?.id || '');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '加载失败');
      if (error instanceof Error && error.message.includes('Token')) {
        logout();
      }
    } finally {
      setLoading(false);
    }
  }

  async function updateStatus(nextStatus: CourtStatus) {
    if (!selectedCourt || nextStatus === 'all') {
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      const updated = await request<Court>(`/api/admin/courts/${selectedCourt.id}/review`, {
        method: 'PATCH',
        body: JSON.stringify({ approvalStatus: nextStatus, reason })
      });
      setCourts((current) => current.map((court) => (court.id === updated.id ? updated : court)));
      setReason('');
      setMessage('已更新审核状态');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '更新失败');
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem('tennerAdminToken');
    setToken('');
    setCourts([]);
    setSelectedId('');
  }

  if (!token) {
    return (
      <main className="login-shell">
        <form className="login-panel" onSubmit={login}>
          <div>
            <p className="eyebrow">Tenner Admin</p>
            <h1>球场审核后台</h1>
          </div>
          <label>
            管理员账号
            <input value={email} onChange={(event) => setEmail(event.target.value)} autoComplete="username" />
          </label>
          <label>
            密码
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
            />
          </label>
          {message && <p className="message error">{message}</p>}
          <button className="primary-button" disabled={loading}>
            {loading ? '登录中' : '登录'}
          </button>
        </form>
      </main>
    );
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Tenner Admin</p>
          <h1>球场审核</h1>
        </div>
        <nav>
          {statusTabs.map((tab) => (
            <button
              key={tab.value}
              className={status === tab.value ? 'nav-item active' : 'nav-item'}
              onClick={() => setStatus(tab.value)}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          ))}
        </nav>
        <button className="ghost-button" onClick={logout}>
          <LogOut size={16} />
          <span>退出</span>
        </button>
      </aside>

      <section className="list-pane">
        <header className="toolbar">
          <div className="search-box">
            <Search size={16} />
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="搜索名称、提交人、小程序" />
          </div>
          <button className="icon-button" title="刷新" onClick={() => loadCourts(status)}>
            <RefreshCcw size={18} />
          </button>
        </header>
        {message && <p className={message.includes('失败') || message.includes('错误') ? 'message error' : 'message'}>{message}</p>}
        <div className="court-list">
          {visibleCourts.map((court) => (
            <button
              key={court.id}
              className={selectedCourt?.id === court.id ? 'court-row selected' : 'court-row'}
              onClick={() => setSelectedId(court.id)}
            >
              <span className={`status-dot ${court.approvalStatus}`} />
              <span>
                <strong>{court.name}</strong>
                <small>{court.submitterName || court.submitterEmail || court.submitterId || '未知提交人'}</small>
              </span>
              <em>{court.approvalStatusText}</em>
            </button>
          ))}
          {!loading && visibleCourts.length === 0 && <div className="empty">暂无球场</div>}
        </div>
      </section>

      <section className="detail-pane">
        {selectedCourt ? (
          <>
            <header className="detail-header">
              <div>
                <span className={`badge ${selectedCourt.approvalStatus}`}>{selectedCourt.approvalStatusText}</span>
                <h2>{selectedCourt.name}</h2>
                <p>{[selectedCourt.country, selectedCourt.city, selectedCourt.address].filter(Boolean).join(' / ') || '未填写地址'}</p>
              </div>
            </header>

            <div className="photo-grid">
              {selectedCourt.photoUrls.slice(0, 5).map((url) => (
                <img key={url} src={url} alt={selectedCourt.name} />
              ))}
              {selectedCourt.photoUrls.length === 0 && <div className="photo-placeholder">无照片</div>}
            </div>

            <div className="info-grid">
              <Info label="提交人" value={selectedCourt.submitterName || selectedCourt.submitterEmail || selectedCourt.submitterId} />
              <Info label="微信小程序" value={selectedCourt.wechatMiniProgramName} />
              <Info label="联系电话" value={selectedCourt.submitterPhone} />
              <Info label="审核人" value={selectedCourt.reviewedBy} />
            </div>

            <div className="description">{selectedCourt.description || '未填写补充描述'}</div>

            <textarea
              value={reason}
              onChange={(event) => setReason(event.target.value)}
              placeholder="审核备注"
              rows={4}
            />
            <div className="actions">
              <button className="approve-button" disabled={loading} onClick={() => updateStatus('approved')}>
                <Check size={16} />
                <span>通过</span>
              </button>
              <button disabled={loading} onClick={() => updateStatus('voided')}>
                <Ban size={16} />
                <span>作废</span>
              </button>
              <button className="danger-button" disabled={loading} onClick={() => updateStatus('blacklisted')}>
                <ShieldX size={16} />
                <span>黑名单</span>
              </button>
            </div>
          </>
        ) : (
          <div className="empty detail-empty">选择一个球场查看详情</div>
        )}
      </section>
    </main>
  );
}

function Info({ label, value }: { label: string; value?: string }) {
  return (
    <div className="info-item">
      <span>{label}</span>
      <strong>{value || '未填写'}</strong>
    </div>
  );
}

createRoot(document.getElementById('root')!).render(<App />);
