import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import Header from '../components/Header';
import JobCard from '../components/JobCard';
import { getJobs, saveAlert, getFacets } from '../services/api'; // <-- added getFacets


type JobDto = {
  id: string;
  title: string;
  company: string;
  location: string;
  source: string;
  applyUrl: string;
  postedAt?: string;
};
type PageResponse<T> = { items: T[]; page: number; size: number; total: number };

type Filters = {
  source: string;               // 'all'|'adzuna'|'remotive'|'naukri'
  postedWithin: string;         // 'any'|'1'|'3'|'7'|'14'|'30'
  companyContains: string;
  sortBy: 'relevance'|'recency';
};

export default function ResultsPage() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchParams, setSearchParams] = useSearchParams();
  const prefId = searchParams.get('prefId') ?? '';

  const [jobs, setJobs] = useState<JobDto[]>([]);
  const [page, setPage] = useState<number>(Number(searchParams.get('page') ?? 0));
  const [size] = useState<number>(10);
  const [total, setTotal] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const [errMsg, setErrMsg] = useState<string | null>(null);

  const [filters, setFilters] = useState<Filters>({
    source: 'all', postedWithin: 'any', companyContains: '', sortBy: 'relevance'
  });

  // pending inputs (Apply button UX)
  const [sourceInput, setSourceInput] = useState('all');
  const [postedWithinInput, setPostedWithinInput] = useState('any');
  const [companyContainsInput, setCompanyContainsInput] = useState('');
  const [sortByInput, setSortByInput] = useState<'relevance'|'recency'>('relevance');

  // facets (counts)
  const [facets, setFacets] = useState<{
    sourceCounts: Record<string, number>,
    recencyCounts: Record<string, number>,
    total: number
  } | null>(null);

  // alert email
  const [alertEmail, setAlertEmail] = useState('');
  const [alertChannel, setAlertChannel] = useState<'EMAIL' | 'WHATSAPP'>('EMAIL');
  const [savingAlert, setSavingAlert] = useState(false);
  const [alertSuccess, setAlertSuccess] = useState(false);
  const [alertFrequency, setAlertFrequency] = useState<'DAILY' | 'EVERY_3_DAYS' | 'WEEKLY'>('DAILY');


  useEffect(() => {
    if (!prefId) { setErrMsg('Missing prefId. Please submit preferences again.'); return; }
    setErrMsg(null);
    setLoading(true);
    (async () => {
      try {
        const data: PageResponse<JobDto> = await getJobs(
          prefId, page, size, {
            source: filters.source,
            postedWithinDays: filters.postedWithin === 'any' ? undefined : Number(filters.postedWithin),
            companyContains: filters.companyContains || undefined,
            sortBy: filters.sortBy
          }
        );
        setJobs(data.items ?? []);
        setTotal(data.total ?? 0);
      } catch (e: any) {
        setErrMsg(e?.message || 'Failed to fetch jobs');
      } finally {
        setLoading(false);
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [prefId, page, size, filters]);

  // fetch facet counts whenever prefId or facet-scope inputs change
  useEffect(() => {
    if (!prefId) return;
    (async () => {
      try {
        const f = await getFacets(prefId, {
          companyContains: companyContainsInput || undefined,
          sortBy: sortByInput
        });
        setFacets(f);
      } catch {
        setFacets(null);
      }
    })();
  }, [prefId, companyContainsInput, sortByInput]);

  // keep URL in sync for back/forward
  useEffect(() => {
    const next = new URLSearchParams(searchParams);
    next.set('prefId', prefId);
    next.set('page', String(page));
    setSearchParams(next, { replace: true });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // apply/clear
  const applyFilters = () => {
    setFilters({
      source: sourceInput,
      postedWithin: postedWithinInput,
      companyContains: companyContainsInput,
      sortBy: sortByInput
    });
    setPage(0);
  };
  const clearFilters = () => {
    setSourceInput('all');
    setPostedWithinInput('any');
    setCompanyContainsInput('');
    setSortByInput('relevance');
    setFilters({ source: 'all', postedWithin: 'any', companyContains: '', sortBy: 'relevance' });
    setPage(0);
  };

  // pagination helpers (First/Last/Jump)
  const totalPages = Math.max(1, Math.ceil(total / size));
  const [jumpInput, setJumpInput] = useState<string>('');
  const gotoFirst = () => setPage(0);
  const gotoPrev  = () => setPage(p => Math.max(0, p - 1));
  const gotoNext  = () => setPage(p => Math.min(totalPages - 1, p + 1));
  const gotoLast  = () => setPage(totalPages - 1);
  const gotoJump  = () => {
    const n = parseInt(jumpInput, 10);
    if (!isNaN(n) && n >= 1 && n <= totalPages) setPage(n - 1);
  };

  async function sendTestAlert() {
    if (!prefId) {
      alert("Please submit preferences first.");
      return;
    }

    try {
      const res = await fetch(
        `http://localhost:3001/alerts/test?prefId=${prefId}&channel=${alertChannel}`
      );
      const text = await res.text();
      alert(text);
    } catch (e: any) {
      alert("❌ Failed to send test alert: " + e.message);
    }
  }


  // alerts
  async function onSaveAlert() {
    if (!prefId) { setErrMsg('Preference not found. Please submit preferences.'); return; }
    if (!alertEmail || !alertEmail.includes('@')) { setErrMsg('Please enter a valid email.'); return; }
    if (!alertEmail.trim()) {
        setErrMsg('Please enter a valid contact.');
        return;
      }

    try {
        setSavingAlert(true);
      setAlertSuccess(false);
      setErrMsg(null);
          await saveAlert(prefId, alertEmail.trim(), alertChannel, alertFrequency);
          setAlertSuccess(true);
      alert('Saved! You will receive job alerts at ' + alertEmail);
    } catch (e: any) {
      setErrMsg(e?.message || 'Failed to save alert');
    }
  }

  return (
    <div className="min-h-screen bg-white">
      <main className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between gap-4 flex-wrap">
          <h1 className="text-2xl font-bold">Job Matches</h1>
          <Link to="/preferences" className="text-blue-600 hover:underline text-sm">Change Preferences</Link>
        </div>

        {/* === Alerts Section === */}
        <div className="mt-6 p-5 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl border border-blue-100 shadow-sm">
          <h2 className="text-lg font-semibold text-gray-800 mb-3 flex items-center gap-2">
            💌 Stay Updated
          </h2>
          <p className="text-sm text-gray-600 mb-4">
            Get notified about new <b>{filters.source === 'all' ? 'matching' : filters.source}</b> jobs as soon as they’re posted.
          </p>

          <div className="flex flex-col sm:flex-row sm:items-end gap-3">
            <div className="flex-1">
              <label className="block text-xs text-gray-600 mb-1">Contact (Email or WhatsApp)</label>
              <input
                type="text"
                placeholder="e.g. you@example.com or +919876543210"
                className="w-full border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                value={alertEmail}
                onChange={e => setAlertEmail(e.target.value)}
              />
            </div>


            <div>
              <label className="block text-xs text-gray-600 mb-1">Channel</label>
              <select
                className="border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                value={alertChannel}
                onChange={e => setAlertChannel(e.target.value as 'EMAIL' | 'WHATSAPP')}
              >
                <option value="EMAIL">Email</option>
                <option value="WHATSAPP">WhatsApp</option>
              </select>
            </div>

            <div>
              <label className="block text-xs text-gray-600 mb-1">Frequency</label>
              <select
                className="border rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
                value={alertFrequency}
                onChange={e => setAlertFrequency(e.target.value as 'DAILY' | 'EVERY_3_DAYS' | 'WEEKLY')}
              >
                <option value="DAILY">Daily</option>
                <option value="EVERY_3_DAYS">Every 3 Days</option>
                <option value="WEEKLY">Weekly</option>
              </select>
            </div>


            <div className="flex items-center">
              <button
                onClick={onSaveAlert}
                disabled={savingAlert}
                className={`flex items-center justify-center gap-2 rounded-lg px-4 py-2 text-white text-sm font-medium shadow-md transition-all ${
                  savingAlert
                    ? 'bg-blue-400 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-700 active:scale-[0.98]'
                }`}
              >
                {savingAlert ? (
                  <>
                    <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                    </svg>
                    Saving...
                  </>
                ) : (
                  <>💾 Save & Get Alerts</>
                )}
              </button>
              <Link
                to="/alerts"
                className="rounded border border-blue-600 text-blue-600 px-4 py-2 hover:bg-blue-50 ml-2"
              >
                Manage Alerts
              </Link>

            </div>
            <div className="flex items-center">
              <button
                onClick={onSaveAlert}
                disabled={savingAlert}
                className={`flex items-center justify-center gap-2 rounded-lg px-4 py-2 text-white text-sm font-medium shadow-md transition-all ${
                  savingAlert
                    ? 'bg-blue-400 cursor-not-allowed'
                    : 'bg-blue-600 hover:bg-blue-700 active:scale-[0.98]'
                }`}
              >
                {savingAlert ? (
                  <>
                    <svg className="animate-spin h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                    </svg>
                    Saving...
                  </>
                ) : (
                  <>💾 Save & Get Alerts</>
                )}
              </button>

              {/* New Test Button */}
              <button
                onClick={sendTestAlert}
                className="ml-3 rounded-lg border border-green-500 text-green-600 px-4 py-2 text-sm font-medium hover:bg-green-50 transition-all"
              >
                🚀 Send Test Alert
              </button>
            </div>

          </div>

          {alertSuccess && (
            <div className="mt-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded-lg px-3 py-2 animate-fadeIn">
              ✅ {alertChannel === 'EMAIL'
                ? `You’ll now receive job updates at ${alertEmail}.`
                : `We’ll send WhatsApp job alerts to ${alertEmail}.`}
            </div>
          )}
        </div>


        {/* Filters */}
        <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 bg-gray-50 border rounded-xl p-3">
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Source</label>
            <select className="border rounded px-2 py-1"
                    value={sourceInput}
                    onChange={e => setSourceInput(e.target.value)}>
              <option value="all">All sources{facets ? ` (${facets.total})` : ''}</option>
              <option value="adzuna">Adzuna{facets ? ` (${facets.sourceCounts?.adzuna ?? 0})` : ''}</option>
              <option value="remotive">Remotive{facets ? ` (${facets.sourceCounts?.remotive ?? 0})` : ''}</option>
              <option value="naukri">Naukri{facets ? ` (${facets.sourceCounts?.naukri ?? 0})` : ''}</option>
            </select>
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Posted within</label>
            <select className="border rounded px-2 py-1"
                    value={postedWithinInput}
                    onChange={e => setPostedWithinInput(e.target.value)}>
              <option value="any">Any time{facets ? ` (${facets.recencyCounts?.any ?? 0})` : ''}</option>
              <option value="1">Last 24 hours{facets ? ` (${facets.recencyCounts?.["1"] ?? 0})` : ''}</option>
              <option value="3">Last 3 days{facets ? ` (${facets.recencyCounts?.["3"] ?? 0})` : ''}</option>
              <option value="7">Last 7 days{facets ? ` (${facets.recencyCounts?.["7"] ?? 0})` : ''}</option>
              <option value="14">Last 14 days{facets ? ` (${facets.recencyCounts?.["14"] ?? 0})` : ''}</option>
              <option value="30">Last 30 days{facets ? ` (${facets.recencyCounts?.["30"] ?? 0})` : ''}</option>
            </select>
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Company contains</label>
            <input className="border rounded px-2 py-1"
                   value={companyContainsInput}
                   onChange={e => setCompanyContainsInput(e.target.value)}
                   placeholder="e.g. Microsoft" />
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Sort</label>
            <select className="border rounded px-2 py-1"
                    value={sortByInput}
                    onChange={e => setSortByInput(e.target.value as any)}>
              <option value="relevance">Relevance (default)</option>
              <option value="recency">Most recent</option>
            </select>
          </div>
          <div className="flex items-end gap-2">
            <button type="button" onClick={applyFilters}
                    className="border rounded px-3 py-1.5 text-sm bg-blue-600 text-white hover:bg-blue-700">
              Apply
            </button>
            <button type="button" onClick={clearFilters}
                    className="border rounded px-3 py-1.5 text-sm">
              Clear
            </button>
          </div>
        </div>

        {loading && <div className="mt-6">Loading jobs…</div>}
        {errMsg && (
          <div className="mt-6 rounded-md border border-red-300 bg-red-50 p-3 text-red-700">{errMsg}</div>
        )}

        {!loading && !errMsg && (
          <>
            <div className="mt-6 grid gap-4">
              {jobs.length === 0 ? (
                <div className="text-gray-600">No jobs found with the current filters.</div>
              ) : (
                jobs.map((j) => (
                  <JobCard
                    key={j.id}
                    title={j.title}
                    company={j.company}
                    location={j.location}
                    source={j.source}
                    applyUrl={j.applyUrl}
                    postedAt={j.postedAt}
                  />
                ))
              )}
            </div>

            {/* Pagination */}
            <div className="mt-8 flex items-center justify-between border-t pt-4 gap-3 flex-wrap">
              <span className="text-sm text-gray-600">
                Page {page + 1} of {totalPages} • {total} result{total === 1 ? '' : 's'}
              </span>
              <div className="flex items-center gap-2">
                <button onClick={gotoFirst} disabled={page === 0}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">⏮ First</button>
                <button onClick={gotoPrev} disabled={page === 0}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">← Prev</button>
                <button onClick={gotoNext} disabled={page + 1 >= totalPages}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">Next →</button>
                <button onClick={gotoLast} disabled={page + 1 >= totalPages}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">Last ⏭</button>

                <div className="flex items-center gap-1 ml-2">
                  <input
                    value={jumpInput}
                    onChange={e => setJumpInput(e.target.value)}
                    placeholder="Go to..."
                    className="w-20 border rounded px-2 py-1 text-sm"
                  />
                  <button onClick={gotoJump} className="rounded-lg border px-3 py-1.5 text-sm">
                    Go
                  </button>
                </div>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
