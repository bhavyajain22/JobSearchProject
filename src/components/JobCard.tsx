// src/components/JobCard.tsx
import React from 'react';

type Props = {
  title: string;
  company: string;
  location: string;
  source: string; // e.g., "LinkedIn" | "Naukri"
  applyUrl: string;
  postedAt?: string;
};

export default function JobCard({
  title,
  company,
  location,
  source,
  applyUrl,
  postedAt,
}: Props) {
  return (
    <div className="rounded-xl border p-4 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="text-lg font-semibold">{title}</div>
          <div className="text-sm text-gray-700">{company}</div>
          <div className="text-sm text-gray-600">{location || '—'}</div>
          {postedAt && (
            <div className="text-xs text-gray-500 mt-1">Posted: {postedAt}</div>
          )}
        </div>
        <span className="text-xs rounded-full bg-gray-100 border px-2 py-1">
          {source}
        </span>
      </div>
      <div className="mt-4">
        <a
          href={applyUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex items-center rounded-lg bg-blue-600 px-3 py-2 text-white hover:bg-blue-700"
        >
          Apply Now
        </a>
      </div>
    </div>
  );
}
