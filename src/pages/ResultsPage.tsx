import React, { useEffect, useMemo, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import Header from '../components/Header';
import JobCard from '../components/JobCard';
import { getJobs } from '../services/api';
import { isWithinDays, parseISOOrNull } from '../utils/dates';

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

  // Pending filter inputs
  const [sourceInput, setSourceInput] = useState<string>('all');
  const [postedWithinInput, setPostedWithinInput] = useState<string>('any');
  const [companyContainsInput, setCompanyContainsInput] = useState<string>('');
  const [sortByInput, setSortByInput] = useState<'relevance'|'recency'>('relevance');

  // Active filters (applied on button click)
  const [filters, setFilters] = useState({
    source: 'all',
    postedWithin: 'any',
    companyContains: '',
    sortBy: 'relevance' as 'relevance'|'recency',
  });

  // Fetch jobs from backend
  useEffect(() => {
    if (!prefId) { setErrMsg('Missing prefId. Please submit preferences again.'); return; }
    setErrMsg(null);
    setLoading(true);
    (async () => {
      try {
        const data: PageResponse<JobDto> = await getJobs(prefId, page, size);
        setJobs(data.items ?? []);
        setTotal(data.total ?? 0);
      } catch (e: any) {
        setErrMsg(e?.message || 'Failed to fetch jobs');
      } finally {
        setLoading(false);
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [prefId, page, size]);

  // Keep URL in sync
  useEffect(() => {
    const next = new URLSearchParams(searchParams);
    next.set('prefId', prefId);
    next.set('page', String(page));
    setSearchParams(next, { replace: true });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  // Derived jobs after filtering/sorting
  const filteredSorted = useMemo(() => {
    let rows = jobs.slice();

    if (filters.source !== 'all') {
      rows = rows.filter(r => (r.source || '').toLowerCase() === filters.source);
    }
    if (filters.postedWithin !== 'any') {
      const days = Number(filters.postedWithin);
      rows = rows.filter(r => isWithinDays(r.postedAt, days));
    }
    if (filters.companyContains.trim()) {
      const q = filters.companyContains.trim().toLowerCase();
      rows = rows.filter(r => (r.company || '').toLowerCase().includes(q));
    }
    if (filters.sortBy === 'recency') {
      rows.sort((a, b) => {
        const da = parseISOOrNull(a.postedAt)?.getTime() ?? 0;
        const db = parseISOOrNull(b.postedAt)?.getTime() ?? 0;
        return db - da;
      });
    }
    return rows;
  }, [jobs, filters]);

  const totalPages = Math.max(1, Math.ceil(total / size));
  const canPrev = page > 0;
  const canNext = page + 1 < totalPages;

  const applyFilters = () => {
    setFilters({
      source: sourceInput,
      postedWithin: postedWithinInput,
      companyContains: companyContainsInput,
      sortBy: sortByInput,
    });
    setPage(0); // reset to first page
  };

  const clearFilters = () => {
    setSourceInput('all');
    setPostedWithinInput('any');
    setCompanyContainsInput('');
    setSortByInput('relevance');
    setFilters({ source: 'all', postedWithin: 'any', companyContains: '', sortBy: 'relevance' });
    setPage(0);
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
              {filteredSorted.length === 0 ? (
                <div className="text-gray-600">No jobs found with the current filters.</div>
              ) : (
                filteredSorted.map((j) => (
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
            <div className="mt-8 flex items-center justify-between border-t pt-4">
              <span className="text-sm text-gray-600">
                Page {page + 1} of {totalPages} • {total} result{total === 1 ? '' : 's'}
              </span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={!canPrev}
                  className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50"
                >
                  ← Prev
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={!canNext}
                  className="rounded-lg border px-3 py-1.5 text-sm disabled:opacity-50"
                >
                  Next →
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
