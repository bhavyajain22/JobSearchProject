import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import Header from '../components/Header';
import JobCard from '../components/JobCard';
import { getJobs } from '../services/api';

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

  return (
    <div className="min-h-screen bg-white">
      <Header mobileMenuOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      <main className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between gap-4 flex-wrap">
          <h1 className="text-2xl font-bold">Job Matches</h1>
          <Link to="/preferences" className="text-blue-600 hover:underline text-sm">Change Preferences</Link>
        </div>

        {/* Filters */}
        <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 bg-gray-50 border rounded-xl p-3">
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Source</label>
            <select className="border rounded px-2 py-1"
                    value={sourceInput}
                    onChange={e => setSourceInput(e.target.value)}>
              <option value="all">All sources</option>
              <option value="adzuna">Adzuna</option>
              <option value="remotive">Remotive</option>
              <option value="naukri">Naukri</option>
            </select>
          </div>
          <div className="flex flex-col">
            <label className="text-xs text-gray-600 mb-1">Posted within</label>
            <select className="border rounded px-2 py-1"
                    value={postedWithinInput}
                    onChange={e => setPostedWithinInput(e.target.value)}>
              <option value="any">Any time</option>
              <option value="1">Last 24 hours</option>
              <option value="3">Last 3 days</option>
              <option value="7">Last 7 days</option>
              <option value="14">Last 14 days</option>
              <option value="30">Last 30 days</option>
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
                <button onClick={() => setPage(0)} disabled={page === 0}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">⏮ First</button>
                <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">← Prev</button>
                <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page + 1 >= totalPages}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">Next →</button>
                <button onClick={() => setPage(totalPages - 1)} disabled={page + 1 >= totalPages}
                        className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50">Last ⏭</button>

                <div className="flex items-center gap-1 ml-2">
                  <input
                    value={jumpInput}
                    onChange={e => setJumpInput(e.target.value)}
                    placeholder="Go to..."
                    className="w-20 border rounded px-2 py-1 text-sm"
                  />
                  <button onClick={() => {
                    const n = parseInt(jumpInput, 10);
                    if (!isNaN(n) && n >= 1 && n <= totalPages) setPage(n - 1);
                  }} className="rounded-lg border px-3 py-1.5 text-sm">
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

  function totalPages() {
    return Math.max(1, Math.ceil(total / size));
  }
}
