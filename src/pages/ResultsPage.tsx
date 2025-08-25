// src/pages/ResultsPage.tsx
import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import Header from '../components/Header';
import JobCard from '../components/JobCard';
import { getJobs } from '../services/api';

type JobDto = {
  id: string; title: string; company: string; location: string;
  source: string; applyUrl: string; postedAt?: string;
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
  }, [prefId, page, size]);

  // keep URL in sync (nice UX)
  useEffect(() => {
    const next = new URLSearchParams(searchParams);
    next.set('prefId', prefId);
    next.set('page', String(page));
    setSearchParams(next, { replace: true });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const totalPages = Math.max(1, Math.ceil(total / size));
  const canPrev = page > 0;
  const canNext = page + 1 < totalPages;

  return (
    <div className="min-h-screen bg-white">
      <Header mobileMenuOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      <main className="max-w-4xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">Job Matches</h1>
          <Link to="/preferences" className="text-blue-600 hover:underline text-sm">Change Preferences</Link>
        </div>

        {loading && <div className="mt-6">Loading jobs…</div>}
        {errMsg && (
          <div className="mt-6 rounded-md border border-red-300 bg-red-50 p-3 text-red-700">{errMsg}</div>
        )}

        {!loading && !errMsg && (
          <>
            <div className="mt-6 grid gap-4">
              {jobs.length === 0 ? (
                <div className="text-gray-600">No jobs found yet. Try changing your filters.</div>
              ) : (
                jobs.map((j) => (
                  <JobCard key={j.id} title={j.title} company={j.company}
                    location={j.location} source={j.source} applyUrl={j.applyUrl} postedAt={j.postedAt} />
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
