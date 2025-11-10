import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Header from "../components/Header";
import axios from "axios";
import { toast } from "react-hot-toast";

type Alert = {
  id: string;
  contact: string;
  channel: string;
  frequency: string;
  createdAt: string;
};

export default function ManageAlerts() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchAlerts() {
    setLoading(true);
    try {
      const res = await axios.get("http://localhost:3001/alerts");
      setAlerts(res.data);
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchAlerts();
  }, []);

  async function handleDelete(id: string) {
    if (!window.confirm("Delete this alert?")) return;
    await axios.delete(`http://localhost:3001/alerts/${id}`);
    setAlerts(a => a.filter(x => x.id !== id));
  }

  return (
    <div className="min-h-screen bg-white">

      <main className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">Manage Alerts</h1>
          <Link to="/preferences" className="text-blue-600 hover:underline">
            + Create New
          </Link>
        </div>

        {loading && <p>Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}

        <table className="min-w-full border mt-4 text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="p-2 border">Contact</th>
              <th className="p-2 border">Channel</th>
              <th className="p-2 border">Frequency</th>
              <th className="p-2 border">Created At</th>
              <th className="p-2 border">Actions</th>
            </tr>
          </thead>
            <tbody>
              {alerts.map(a => (
                <AlertRow key={a.id} alert={a} onUpdated={fetchAlerts} onDeleted={handleDelete} />
              ))}
            </tbody>


        </table>
      </main>
    </div>
  );
}

toast.success("Alert updated!");
toast.error("Failed to update alert");

type AlertRowProps = {
  alert: Alert;
  onUpdated: () => void;
  onDeleted: (id: string) => void;
};

function AlertRow({ alert, onUpdated, onDeleted }: AlertRowProps) {
  const [isEditing, setIsEditing] = React.useState(false);
  const [contact, setContact] = React.useState(alert.contact);
  const [channel, setChannel] = React.useState(alert.channel);
  const [frequency, setFrequency] = React.useState(alert.frequency);
  const [loading, setLoading] = React.useState(false);

  async function handleSave() {
    try {
      setLoading(true);
      await axios.put(`http://localhost:3001/alerts/${alert.id}`, {
        contact,
        channel,
        frequency,
      });
      setIsEditing(false);
      onUpdated();
    } catch (err) {
      alert("Failed to update alert");
    } finally {
      setLoading(false);
    }
  }

  return (
    <tr className="text-center border-b">
      <td className="border p-2">
        {isEditing ? (
          <input
            value={contact}
            onChange={e => setContact(e.target.value)}
            className="border rounded px-2 py-1 w-full"
          />
        ) : (
          alert.contact
        )}
      </td>
      <td className="border p-2">
        {isEditing ? (
          <select
            value={channel}
            onChange={e => setChannel(e.target.value)}
            className="border rounded px-2 py-1 w-full"
          >
            <option value="EMAIL">Email</option>
            <option value="WHATSAPP">WhatsApp</option>
          </select>
        ) : (
          alert.channel
        )}
      </td>
      <td className="border p-2">
        {isEditing ? (
          <select
            value={frequency}
            onChange={e => setFrequency(e.target.value)}
            className="border rounded px-2 py-1 w-full"
          >
            <option value="DAILY">Daily</option>
            <option value="EVERY_3_DAYS">Every 3 Days</option>
            <option value="WEEKLY">Weekly</option>
          </select>
        ) : (
          alert.frequency
        )}
      </td>
      <td className="border p-2">
        {new Date(alert.createdAt).toLocaleDateString()}
      </td>
      <td className="border p-2 flex gap-2 justify-center">
        {isEditing ? (
          <>
            <button
              onClick={handleSave}
              disabled={loading}
              className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 disabled:opacity-50"
            >
              {loading ? "Saving..." : "Save"}
            </button>
            <button
              onClick={() => setIsEditing(false)}
              className="border px-3 py-1 rounded hover:bg-gray-100"
            >
              Cancel
            </button>
          </>
        ) : (
          <>
            <button
              onClick={() => setIsEditing(true)}
              className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600"
            >
              Edit
            </button>
            <button
              onClick={() => onDeleted(alert.id)}
              className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
            >
              Delete
            </button>
          </>
        )}
      </td>
    </tr>
  );
}

