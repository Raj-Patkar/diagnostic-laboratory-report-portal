import { useCallback, useEffect, useState } from "react";
import "./App.css";

const API = import.meta.env.VITE_API_BASE_URL || "http://localhost:8085";

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    credentials: "include",
    ...options,
    headers: { ...(options.body ? { "Content-Type": "application/json" } : {}), ...options.headers },
  });
  const type = response.headers.get("content-type") || "";
  const data = type.includes("application/json") ? await response.json() : await response.text();
  if (!response.ok) throw new Error(typeof data === "string" ? data : data?.message || `Request failed (${response.status})`);
  return data;
}

const nav = [
  ["dashboard", "Dashboard", "▦"],
  ["patients", "Patients", "♙"],
  ["tests", "Lab tests", "⚕"],
  ["reports", "Reports", "▤"],
  ["activity", "Activity log", "◷"],
];

function Field({ label, children }) {
  return <label className="field"><span>{label}</span>{children}</label>;
}

function Login({ onLogin, error, busy, onBack }) {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("");
  return <main className="login-page">
    <section className="login-art">
      <div className="brand"><span className="brand-logo">D<span>+</span></span><span><b>DiagLab</b><small>Laboratory portal</small></span></div>
      <div className="login-copy"><span className="eyebrow">DIAGNOSTIC LABORATORY REPORT PORTAL</span><h1>Clear workflows.<br />Better care.</h1><p>Manage patients, track laboratory tests, and organize diagnostic reports in one secure workspace.</p><ul><li>Patient and test tracking</li><li>Centralized report management</li><li>Role-based access</li></ul></div>
      <small className="login-foot">Academic MVP · Diagnostic Laboratory Portal</small>
    </section>
    <section className="login-content"><form className="login-card" onSubmit={e => { e.preventDefault(); onLogin({ username: username.trim(), password }); }}>
      <div className="brand mobile-brand"><span className="brand-logo">D<span>+</span></span><span><b>DiagLab</b><small>Laboratory portal</small></span></div>
      <span className="eyebrow">WELCOME BACK</span><h2>Sign in to your account</h2><p className="muted">Enter your portal credentials to continue.</p>
      {error && <div className="error-box">{error}</div>}
      <Field label="Username"><input value={username} onChange={e => setUsername(e.target.value)} autoComplete="username" required maxLength={50} /></Field>
      <Field label="Password"><input type="password" value={password} onChange={e => setPassword(e.target.value)} autoComplete="current-password" required maxLength={100} /></Field>
      <button className="btn primary full" disabled={busy}>{busy ? "Signing in…" : "Sign in"} <span>→</span></button>
      <p className="login-note">Access is restricted to authorized laboratory staff.</p>
      <button
        type="button"
        className="login-back"
        onClick={onBack}
      >
        ← Back to home
      </button>
    </form></section>
  </main>;
}


function Landing({ onLogin }) {
  return (
    <div className="landing-page">
      <header className="landing-header">
        <a href="#home" className="landing-brand">
          <span className="brand-logo">
            D<span>+</span>
          </span>
          <span>
            <b>DiagLab</b>
            <small>Laboratory portal</small>
          </span>
        </a>

        <nav className="landing-nav">
          <a href="#features">Features</a>
          <a href="#workflow">How it works</a>
          <button className="btn primary" onClick={onLogin}>
            Sign in <span>→</span>
          </button>
        </nav>
      </header>

      <main id="home">
        <section className="landing-hero">
          <div className="landing-hero-copy">
            <span className="eyebrow">
              DIAGNOSTIC LABORATORY REPORT PORTAL
            </span>

            <h1>
              Laboratory workflows,
              <br />
              <span>made simpler.</span>
            </h1>

            <p>
              A centralized workspace to manage patient records, track
              laboratory tests, and organize diagnostic reports with
              role-based access.
            </p>

            <div className="landing-actions">
              <button className="btn primary landing-cta" onClick={onLogin}>
                Access your portal <span>→</span>
              </button>
              <a className="landing-secondary" href="#features">
                Explore features ↓
              </a>
            </div>

            <div className="landing-trust">
              <span>✓</span>
              Role-based access
              <span>✓</span>
              Centralized records
            </div>
          </div>

          <div className="landing-visual" aria-label="Laboratory portal overview">
            <div className="visual-orbit orbit-one" />
            <div className="visual-orbit orbit-two" />

            <div className="visual-card visual-main">
              <div className="visual-card-top">
                <span className="visual-mark">D+</span>
                <div>
                  <b>Laboratory overview</b>
                  <small>Workspace preview</small>
                </div>
                <span className="online-dot" />
              </div>

              <div className="visual-banner">
                <small>DIAGLAB WORKSPACE</small>
                <h3>Everything in one place.</h3>
                <p>Patients, tests and reports.</p>
              </div>

              <div className="visual-metrics">
                <div>
                  <span className="metric-icon metric-blue">♙</span>
                  <small>Patients</small>
                  <b>Records</b>
                </div>
                <div>
                  <span className="metric-icon metric-purple">⚕</span>
                  <small>Lab tests</small>
                  <b>Tracking</b>
                </div>
                <div>
                  <span className="metric-icon metric-green">▤</span>
                  <small>Reports</small>
                  <b>Management</b>
                </div>
              </div>

              <div className="visual-footer">
                <span className="visual-check">✓</span>
                <span>
                  <b>Secure workspace</b>
                  <small>Access based on assigned roles</small>
                </span>
              </div>
            </div>

            <div className="floating-note">
              <span>✓</span>
              <div>
                <b>Organized workflows</b>
                <small>From test request to report</small>
              </div>
            </div>
          </div>
        </section>

        <section id="features" className="landing-features">
          <div className="landing-section-heading">
            <span className="eyebrow">BUILT FOR CLARITY</span>
            <h2>Your laboratory workflow, organized.</h2>
            <p>
              Keep the key steps of laboratory record management together
              in one easy-to-use portal.
            </p>
          </div>

          <div className="feature-grid">
            <article className="feature-card">
              <span className="feature-icon feature-blue">♙</span>
              <h3>Patient management</h3>
              <p>
                Create patient records and search existing information
                from a centralized workspace.
              </p>
            </article>

            <article className="feature-card">
              <span className="feature-icon feature-purple">⚕</span>
              <h3>Laboratory test tracking</h3>
              <p>
                Register test requests and track progress from pending
                through completion.
              </p>
            </article>

            <article className="feature-card">
              <span className="feature-icon feature-green">▤</span>
              <h3>Report management</h3>
              <p>
                Create draft reports for completed tests and manage
                their finalization.
              </p>
            </article>

            <article className="feature-card">
              <span className="feature-icon feature-cyan">◷</span>
              <h3>Activity and access</h3>
              <p>
                Review recorded portal activity and use role-based
                permissions to control access.
              </p>
            </article>
          </div>
        </section>

        <section id="workflow" className="workflow-section">
          <div>
            <span className="eyebrow">A CLEARER WORKFLOW</span>
            <h2>From patient record to report.</h2>
            <p>
              Follow a structured sequence for managing laboratory
              records in the portal.
            </p>
          </div>

          <div className="workflow-steps">
            <div><span>01</span><b>Register</b><p>Create or find a patient record.</p></div>
            <div><span>02</span><b>Track</b><p>Register a test and update its status.</p></div>
            <div><span>03</span><b>Manage</b><p>Create and finalize the associated report.</p></div>
          </div>
        </section>

        <section className="landing-bottom-cta">
          <div>
            <h2>Ready to access your workspace?</h2>
            <p>Sign in with your authorized DiagLab account.</p>
          </div>
          <button className="btn primary landing-cta" onClick={onLogin}>
            Sign in to DiagLab <span>→</span>
          </button>
        </section>
      </main>

      <footer className="landing-footer">
        <span>© {new Date().getFullYear()} DiagLab</span>
        <span>Diagnostic Laboratory Report Portal · Academic MVP</span>
      </footer>
    </div>
  );
}


function App() {
  const [publicPage, setPublicPage] = useState("landing");
  const [user, setUser] = useState(null);
  const [page, setPage] = useState("dashboard");
  const [menuOpen, setMenuOpen] = useState(false);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [toast, setToast] = useState("");
  const [refresh, setRefresh] = useState(0);
  const [loading, setLoading] = useState(false);
  const [dashboard, setDashboard] = useState({});
  const [patients, setPatients] = useState([]);
  const [tests, setTests] = useState([]);
  const [reports, setReports] = useState([]);
  const [activities, setActivities] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("ALL");
  const [modal, setModal] = useState("");
  const [saving, setSaving] = useState(false);

  const notify = message => { setToast(message); window.setTimeout(() => setToast(""), 3500); };
  const loadMe = useCallback(async () => {
    try { const data = await request("/api/auth/me"); setUser({ username: data.username, role: data.role }); }
    catch { setUser(null); }
  }, []);
  useEffect(() => { loadMe(); }, [loadMe]);

  const load = useCallback(async () => {
    if (!user) return;
    setLoading(true);
    try {
      if (page === "dashboard") {
        const d = await request("/api/dashboard");
        setDashboard(d?.summary || d || {});
        setActivities(Array.isArray(d?.recentActivity) ? d.recentActivity : []);
      } else if (page === "patients") {
        setPatients(await request(`/api/patients${search.trim() ? `?search=${encodeURIComponent(search.trim())}` : ""}`));
      } else if (page === "tests") {
        const p = new URLSearchParams();
        if (filter !== "ALL") p.set("status", filter);
        if (search.trim()) p.set("search", search.trim());
        setTests(await request(`/api/tests${p.size ? `?${p}` : ""}`));
      } else if (page === "reports") {
        setReports(await request(`/api/reports${filter !== "ALL" ? `?status=${filter}` : ""}`));
      } else if (page === "activity") {
        setActivities(await request("/api/activity"));
      }
    } catch (e) { notify(e.message || "Could not load data."); }
    finally { setLoading(false); }
  }, [user, page, search, filter, refresh]);
  useEffect(() => { load(); }, [load]);

  async function login(credentials) {
    setBusy(true); setError("");
    try {
      const d = await request("/api/auth/login", { method: "POST", body: JSON.stringify(credentials) });
      setUser({ username: d.username, role: d.role }); setPage("dashboard"); notify("Signed in successfully.");
    } catch (e) { setError(e.message || "Unable to sign in."); }
    finally { setBusy(false); }
  }
  async function logout() {
    try {
      await request("/api/auth/logout", { method: "POST" });
    } catch (e) {
      notify(e.message || "Sign-out request failed.");
    }

    setUser(null);
    setPage("dashboard");
    setSearch("");
    setFilter("ALL");
    setPublicPage("landing");
  }
  async function saveRecord(event) {
    event.preventDefault(); const form = new FormData(event.currentTarget); const v = Object.fromEntries(form.entries());
    setSaving(true);
    try {
      let path = "", method = "POST", body = {};
      if (modal === "patient") {
        path = "/api/patients";
        body = { patientCode: v.patientCode, fullName: v.fullName, dateOfBirth: v.dateOfBirth || null, gender: v.gender, phone: v.phone, email: v.email, address: v.address };
      } else if (modal === "test") {
        path = "/api/tests";
        body = { patientId: Number(v.patientId), testName: v.testName, testType: v.testType, requestedBy: v.requestedBy, technician: v.technician };
      } else {
        path = "/api/reports";
        body = { testId: Number(v.testId), result: v.result, referenceRange: v.referenceRange, remarks: v.remarks, status: "DRAFT" };
      }
      await request(path, { method, body: JSON.stringify(body) });
      setModal(""); setRefresh(x => x + 1); notify("Record saved successfully.");
    } catch (e) { notify(e.message || "Could not save record."); }
    finally { setSaving(false); }
  }
  async function updateTest(test, status) {
    try { await request(`/api/tests/${test.id}/status?status=${status}`, { method: "PUT" }); setRefresh(x => x + 1); notify("Test status updated."); }
    catch (e) { notify(e.message); }
  }
  async function finalizeReport(report) {
    try { await request(`/api/reports/${report.id}`, { method: "PUT", body: JSON.stringify({ ...report, status: "FINAL" }) }); setRefresh(x => x + 1); notify("Report finalized."); }
    catch (e) { notify(e.message); }
  }

  if (!user) {
    if (publicPage === "login") {
      return (
        <Login
          onLogin={login}
          error={error}
          busy={busy}
          onBack={() => {
            setError("");
            setPublicPage("landing");
          }}
        />
      );
    }

    return <Landing onLogin={() => setPublicPage("login")} />;
  }
  const active = nav.find(n => n[0] === page);
  const admin = user.role === "ADMIN";
  const visibleNav = nav.filter(n => n[0] !== "activity" || admin);
  const stats = [
    ["Total patients", dashboard.totalPatients ?? dashboard.patientCount ?? 0, "♙", "blue"],
    ["Lab tests", dashboard.totalTests ?? dashboard.testCount ?? 0, "⚕", "purple"],
    ["Reports created", dashboard.totalReports ?? dashboard.reportCount ?? 0, "▤", "cyan"],
    ["Final reports", dashboard.finalReports ?? 0, "✓", "green"],
  ];

  return <div className="shell">
    {menuOpen && <button className="scrim" aria-label="Close menu" onClick={() => setMenuOpen(false)} />}
    <aside className={`sidebar ${menuOpen ? "open" : ""}`}>
      <div className="brand sidebar-brand"><span className="brand-logo">D<span>+</span></span><span><b>DiagLab</b><small>Laboratory portal</small></span><button className="close-menu" onClick={() => setMenuOpen(false)}>×</button></div>
      <div className="nav-caption">WORKSPACE</div>
      <nav>{visibleNav.map(([id, label, icon]) => <button key={id} className={`nav-link ${page === id ? "selected" : ""}`} onClick={() => { setPage(id); setMenuOpen(false); setSearch(""); setFilter("ALL"); }}><span>{icon}</span>{label}{id === "activity" && <small>ADMIN</small>}</button>)}</nav>
      <div className="sidebar-bottom"><div className="secure-box"><span>✓</span><div><b>Secure workspace</b><small>Role-based access enabled</small></div></div><div className="user-row"><span className="avatar">{user.username[0]?.toUpperCase()}</span><span className="user-meta"><b>{user.username}</b><small>{user.role}</small></span>
        <button
          className="logout"
          onClick={logout}
          title="Sign out"
          aria-label="Sign out"
        >
          <svg
            viewBox="0 0 24 24"
            width="19"
            height="19"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
          >
            <path d="M10 17l5-5-5-5" />
            <path d="M15 12H3" />
            <path d="M12 3h6a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-6" />
          </svg>
        </button>
      </div></div>
    </aside>
    <div className="main-area">
      <header className="topbar"><button className="hamburger" onClick={() => setMenuOpen(true)} aria-label="Open menu">☰</button><div className="crumb"><span>DiagLab</span><i>/</i><b>{active?.[1]}</b></div><div className="top-status"><span /> Laboratory workspace <b className="top-avatar">{user.username[0]?.toUpperCase()}</b></div></header>
      <main className="content">
        <div className="page-title"><div><span className="eyebrow">LABORATORY WORKSPACE</span><h1>{active?.[1]}</h1><p>{({ dashboard: "Monitor patients, laboratory tests, and diagnostic reports.", patients: "Maintain patient details and find records quickly.", tests: "Track requested tests and update their processing status.", reports: "Create reports for completed tests and manage finalization.", activity: "Review actions performed in the portal." })[page]}</p></div><button className="btn secondary refresh" onClick={() => setRefresh(x => x + 1)} disabled={loading}>↻ <span>Refresh</span></button></div>
        {toast && <div className="toast" role="status">{toast}<button onClick={() => setToast("")}>×</button></div>}

        {page === "dashboard" && <div className="stack">
          <section className="welcome"><span className="eyebrow">YOUR LAB, AT A GLANCE</span><h2>Good to see you, {user.username}.</h2><p>Here is the latest overview of your laboratory operations.</p><div className="welcome-plus">+</div></section>
          <section className="stats">{stats.map(([label, value, icon, color]) => <article className="stat" key={label}><span className={`stat-icon ${color}`}>{icon}</span><span className="stat-label">{label}</span><b>{typeof value === "object" ? "—" : value}</b><small>Current records</small></article>)}</section>
          <section className="panel"><div className="panel-heading"><div><h2>Recent activity</h2><p>Latest actions recorded in the portal</p></div>{admin && <button className="link-button" onClick={() => setPage("activity")}>View activity →</button>}</div><Activity items={activities.slice(0, 5)} loading={loading} /></section>
          <section><div className="panel-heading section-head"><div><h2>Quick actions</h2><p>Jump straight into common tasks</p></div></div><div className="quick-actions"><button onClick={() => { setPage("patients"); setModal("patient"); }}><span className="quick-icon blue">♙</span><span><b>Add a patient</b><small>Create a patient record</small></span><strong>→</strong></button><button onClick={() => { setPage("tests"); setModal("test"); }}><span className="quick-icon purple">⚕</span><span><b>Request a lab test</b><small>Register a new test</small></span><strong>→</strong></button><button onClick={() => { setPage("reports"); setModal("report"); }}><span className="quick-icon green">▤</span><span><b>Create a report</b><small>For a completed test</small></span><strong>→</strong></button></div></section>
        </div>}

        {page === "patients" && <section className="panel data-panel"><div className="toolbar"><div className="search"><span>⌕</span><input placeholder="Search patients…" value={search} onChange={e => setSearch(e.target.value)} /></div><button className="btn primary" onClick={() => setModal("patient")}>＋ Add patient</button></div><div className="table-scroll"><table><thead><tr><th>Patient</th><th>Code</th><th>Date of birth</th><th>Phone</th><th>Gender</th></tr></thead><tbody>{patients.map(p => <tr key={p.id}><td><b>{p.fullName}</b><small>{p.email || "No email"}</small></td><td><code>{p.patientCode}</code></td><td>{p.dateOfBirth || "—"}</td><td>{p.phone || "—"}</td><td>{p.gender || "—"}</td></tr>)}</tbody></table>{loading && <p className="loading">Loading patients…</p>}{!loading && !patients.length && <Empty title="No patients found" text="Add a patient or change your search." />}</div><div className="table-foot">{patients.length} patient record(s)</div></section>}

        {page === "tests" && <section className="panel data-panel"><div className="toolbar"><div className="search"><span>⌕</span><input placeholder="Search tests…" value={search} onChange={e => setSearch(e.target.value)} /></div><div className="toolbar-right"><select value={filter} onChange={e => setFilter(e.target.value)}><option value="ALL">All statuses</option><option>PENDING</option><option>IN_PROGRESS</option><option>COMPLETED</option></select><button className="btn primary" onClick={async () => { try { setPatients(await request("/api/patients")); } catch (e) { notify(e.message); } setModal("test"); }}>＋ New test</button></div></div><div className="table-scroll"><table><thead><tr><th>Test</th><th>Patient ID</th><th>Requested by</th><th>Technician</th><th>Requested</th><th>Status</th><th>Update</th></tr></thead><tbody>{tests.map(t => <tr key={t.id}><td><b>{t.testName}</b><small>{t.testType}</small></td><td>#{t.patientId}</td><td>{t.requestedBy || "—"}</td><td>{t.technician || "—"}</td><td>{date(t.requestedAt)}</td><td><Status status={t.status} /></td><td><select className="small-select" value={t.status} onChange={e => updateTest(t, e.target.value)}><option value="PENDING">Pending</option><option value="IN_PROGRESS">In progress</option><option value="COMPLETED">Completed</option></select></td></tr>)}</tbody></table>{loading && <p className="loading">Loading tests…</p>}{!loading && !tests.length && <Empty title="No tests found" text="Create a lab test to start tracking work." />}</div><div className="table-foot">{tests.length} test record(s)</div></section>}

        {page === "reports" && <section className="panel data-panel"><div className="toolbar"><div><b>Diagnostic reports</b><small className="block-muted">Drafts and finalized reports</small></div><div className="toolbar-right"><select value={filter} onChange={e => setFilter(e.target.value)}><option value="ALL">All reports</option><option>DRAFT</option><option>FINAL</option></select><button className="btn primary" onClick={async () => { try { setTests(await request("/api/tests")); } catch (e) { notify(e.message); } setModal("report"); }}>＋ New report</button></div></div><div className="table-scroll"><table><thead><tr><th>Report</th><th>Test ID</th><th>Result</th><th>Reference range</th><th>Status</th><th>Action</th></tr></thead><tbody>{reports.map(r => <tr key={r.id}><td><code>RPT-{String(r.id).padStart(4, "0")}</code><small>Report ID {r.id}</small></td><td>#{r.testId}</td><td className="result">{r.result || "—"}</td><td>{r.referenceRange || "—"}</td><td><Status status={r.status} /></td><td>{r.status === "DRAFT" ? <button className="table-action" onClick={() => finalizeReport(r)}>Finalize</button> : "—"}</td></tr>)}</tbody></table>{loading && <p className="loading">Loading reports…</p>}{!loading && !reports.length && <Empty title="No reports found" text="Create a report for a completed test." />}</div><div className="table-foot">{reports.length} report record(s)</div></section>}

        {page === "activity" && <section className="panel activity-panel"><div className="panel-heading"><div><h2>Audit trail</h2><p>Events recorded by the backend</p></div><span className="count">{activities.length} events</span></div><Activity items={activities} loading={loading} /></section>}
      </main>
      <footer className="footer"><span>DiagLab · Diagnostic Laboratory Report Portal</span><span>Academic MVP <i /> Light mode</span></footer>
    </div>

    {modal && <div className="modal-backdrop" onMouseDown={e => e.target === e.currentTarget && setModal("")}><section className="modal"><div className="modal-title"><h2>Create {modal === "patient" ? "patient" : modal === "test" ? "lab test" : "report"}</h2><button onClick={() => setModal("")}>×</button></div><form onSubmit={saveRecord}>
      {modal === "patient" && <><div className="form-grid"><Field label="Patient code"><input name="patientCode" placeholder="PAT-003" required /></Field><Field label="Full name"><input name="fullName" required /></Field><Field label="Date of birth"><input type="date" name="dateOfBirth" /></Field><Field label="Gender"><select name="gender" required defaultValue=""><option value="" disabled>Select gender</option><option>Female</option><option>Male</option><option>Other</option><option>Prefer not to say</option></select></Field><Field label="Phone"><input name="phone" /></Field><Field label="Email"><input name="email" type="email" /></Field></div><Field label="Address"><textarea name="address" rows="2" /></Field></>}
      {modal === "test" && <><Field label="Patient"><select name="patientId" required defaultValue=""><option value="" disabled>Select patient</option>{patients.map(p => <option key={p.id} value={p.id}>{p.patientCode} — {p.fullName}</option>)}</select></Field><div className="form-grid"><Field label="Test name"><input name="testName" placeholder="Complete Blood Count" required /></Field><Field label="Test type"><input name="testType" placeholder="Hematology" required /></Field><Field label="Requested by"><input name="requestedBy" required /></Field><Field label="Technician"><input name="technician" /></Field></div><p className="form-note">New test requests start with Pending status.</p></>}
      {modal === "report" && <><Field label="Completed test"><select name="testId" required defaultValue=""><option value="" disabled>Select completed test</option>{tests.filter(t => t.status === "COMPLETED").map(t => <option key={t.id} value={t.id}>#{t.id} — {t.testName}</option>)}</select></Field><Field label="Result"><textarea name="result" rows="3" required placeholder="Sample/demo result text" /></Field><Field label="Reference range"><input name="referenceRange" /></Field><Field label="Remarks"><textarea name="remarks" rows="2" /></Field><p className="form-note">Use demo data only; this academic MVP is not a clinical diagnostic system.</p></>}
      <div className="modal-actions"><button type="button" className="btn secondary" onClick={() => setModal("")}>Cancel</button><button className="btn primary" disabled={saving}>{saving ? "Saving…" : "Save record"}</button></div>
    </form></section></div>}
  </div>;
}

function Activity({ items, loading }) {
  if (loading && !items.length) return <p className="loading">Loading activity…</p>;
  if (!items.length) return <div className="empty-activity"><span>◷</span><div><b>No activity yet</b><p>Actions will appear here as users work in the portal.</p></div></div>;
  return <div className="activity-list">{items.map((a, i) => <div className="activity-row" key={a.id ?? i}><span className="activity-icon">{symbol(a.action)}</span><div><b>{String(a.action || "Activity").replaceAll("_", " ")}</b><p>{a.details || "Portal activity recorded"}</p><small>{a.username || "System"} · {date(a.createdAt, true)}</small></div><i /></div>)}</div>;
}
function Empty({ title, text }) { return <div className="empty"><span>＋</span><b>{title}</b><p>{text}</p></div>; }
function Status({ status }) { const s = String(status || "UNKNOWN"); const c = s === "COMPLETED" || s === "FINAL" ? "success" : s === "PENDING" ? "warning" : s === "DRAFT" || s === "IN_PROGRESS" ? "info" : ""; return <span className={`status ${c}`}>{s.replaceAll("_", " ")}</span>; }
function symbol(action) { return String(action || "").includes("PATIENT") ? "♙" : String(action || "").includes("TEST") ? "⚕" : String(action || "").includes("REPORT") ? "▤" : "◷"; }
function date(value, time = false) { if (!value) return "—"; const d = new Date(value); return Number.isNaN(d.getTime()) ? value : new Intl.DateTimeFormat("en-IN", time ? { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" } : { day: "2-digit", month: "short", year: "numeric" }).format(d); }

export default App;
