"use client"

import React, { useEffect, useState } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Users, Library, Activity, Trash2, Edit3, Plus, Search } from "lucide-react"

type User = { id: number; username: string; email: string; role: string }
type Book = { id: number; title: string; author: string; isbn: string; totalCopies: number; availableCopies: number }
type BorrowRecord = { recordId: number; bookTitle: string; issueDate: string; returnDate: string | null; dueDate: string; fine: number; status: string; username: string; finePaid: boolean }

export function AdminDashboard({ user }: { user: User }) {
    const [activeTab, setActiveTab] = useState<"overview" | "books" | "users">("overview")
    const [users, setUsers] = useState<User[]>([])
    const [books, setBooks] = useState<Book[]>([])
    const [allRecords, setAllRecords] = useState<BorrowRecord[]>([])

    // New Book Form
    const [newTitle, setNewTitle] = useState("")
    const [newAuthor, setNewAuthor] = useState("")
    const [newIsbn, setNewIsbn] = useState("")
    const [newCopies, setNewCopies] = useState("")

    const loadData = async () => {
        try {
            const [uRes, bRes, rRes] = await Promise.all([
                fetch("http://localhost:8080/api/users"),
                fetch("http://localhost:8080/api/books"),
                fetch("http://localhost:8080/api/borrow/all")
            ])
            if (uRes.ok) setUsers(await uRes.json())
            if (bRes.ok) setBooks(await bRes.json())
            if (rRes.ok) setAllRecords(await rRes.json())
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => { loadData() }, [])

    const handleAddBook = async () => {
        if (!newTitle || !newAuthor || !newIsbn || !newCopies) return alert("Fill all book fields")
        const res = await fetch("http://localhost:8080/api/books", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title: newTitle, author: newAuthor, isbn: newIsbn, totalCopies: parseInt(newCopies) })
        })
        if (res.ok) {
            setNewTitle(""); setNewAuthor(""); setNewIsbn(""); setNewCopies(""); loadData()
        } else {
            alert("Failed to add book")
        }
    }

    const handleDeleteBook = async (id: number) => {
        if (!confirm("Are you sure you want to delete this book?")) return
        const res = await fetch(`http://localhost:8080/api/books/${id}`, { method: "DELETE" })
        if (res.ok) loadData()
        else alert("Failed to delete book")
    }

    const handleEditBook = async (b: Book) => {
        const title = prompt("New Title:", b.title)
        if (!title) return
        const copies = parseInt(prompt("Total Copies:", b.totalCopies.toString()) || "0")
        if (copies <= 0) return

        const res = await fetch(`http://localhost:8080/api/books/${b.id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title, author: b.author, isbn: b.isbn, totalCopies: copies })
        })
        if (res.ok) loadData()
        else alert("Failed to update book")
    }

    return (
        <div className="space-y-8 animate-in fade-in duration-500 max-w-7xl mx-auto px-4">
            {/* DB TABS */}
            <div className="flex flex-wrap gap-3 border-b border-gray-200 pb-4 pt-4">
                <Button onClick={() => setActiveTab("overview")} className={`rounded-full transition-all text-sm font-semibold shadow-sm ${activeTab === 'overview' ? 'bg-black text-white' : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'}`}>
                    <Activity className="mr-2 h-4 w-4" /> Global Records
                </Button>
                <Button onClick={() => setActiveTab("books")} className={`rounded-full transition-all text-sm font-semibold shadow-sm ${activeTab === 'books' ? 'bg-black text-white' : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'}`}>
                    <Library className="mr-2 h-4 w-4" /> Manage Books
                </Button>
                <Button onClick={() => setActiveTab("users")} className={`rounded-full transition-all text-sm font-semibold shadow-sm ${activeTab === 'users' ? 'bg-black text-white' : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'}`}>
                    <Users className="mr-2 h-4 w-4" /> Registered Users
                </Button>
            </div>

            {/* OVERVIEW CONTENT */}
            {activeTab === "overview" && (
                <section className="space-y-4 animate-in slide-in-from-bottom-2 fade-in duration-500">
                    <h2 className="text-2xl font-bold tracking-tight text-gray-800 flex items-center gap-2">
                        <Activity className="text-black" /> All Borrow Records
                    </h2>
                    <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden">
                        <Table>
                            <TableHeader className="bg-gray-50/80">
                                <TableRow className="hover:bg-transparent border-b-gray-200">
                                    <TableHead className="font-bold text-gray-700 py-4">User</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4">Book</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4">Status</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4 text-right">Fine</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {allRecords.map(r => (
                                    <TableRow key={r.recordId} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                                        <TableCell className="font-semibold text-gray-900">{r.username}</TableCell>
                                        <TableCell>
                                            {r.bookTitle}
                                            <span className="block text-xs text-gray-400 mt-1 font-mono">ID: {r.recordId}</span>
                                        </TableCell>
                                        <TableCell>
                                            <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-semibold ${r.status === 'ISSUED' ? 'bg-blue-50 text-blue-700 border border-blue-200' : 'bg-green-50 text-green-700 border border-green-200'}`}>
                                                {r.status}
                                            </span>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            {r.fine === 0 ? (
                                                <span className="font-bold text-gray-900">₹0</span>
                                            ) : r.finePaid ? (
                                                <span className="font-bold text-green-600 tracking-tight">₹{r.fine} PAID</span>
                                            ) : (
                                                <span className="font-bold text-red-500 animate-pulse tracking-tight">₹{r.fine} DUE</span>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </section>
            )}

            {/* BOOKS CONTENT */}
            {activeTab === "books" && (
                <section className="grid grid-cols-1 xl:grid-cols-3 gap-8 animate-in slide-in-from-bottom-2 fade-in duration-500">
                    <div className="xl:col-span-2 space-y-4">
                        <h2 className="text-2xl font-bold tracking-tight text-gray-800 flex items-center gap-2">
                            <Library className="text-black" /> Library Inventory
                        </h2>
                        <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden">
                            <Table>
                                <TableHeader className="bg-gray-50/80">
                                    <TableRow className="hover:bg-transparent border-b-gray-200">
                                        <TableHead className="font-bold text-gray-700 py-4">Book</TableHead>
                                        <TableHead className="font-bold text-gray-700 py-4 text-center">Stock</TableHead>
                                        <TableHead className="font-bold text-gray-700 py-4 text-right">Action</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {books.map(b => (
                                        <TableRow key={b.id} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                                            <TableCell>
                                                <p className="font-bold text-gray-900 leading-tight">{b.title}</p>
                                                <p className="text-sm text-gray-500 mt-1 flex items-center gap-1">ISBN: <span className="font-mono text-xs">{b.isbn}</span></p>
                                            </TableCell>
                                            <TableCell className="text-center">
                                                <div className="inline-flex items-center justify-center px-3 py-1 rounded-full bg-gray-100 text-sm font-semibold text-gray-700 border border-gray-200">
                                                    {b.availableCopies} / {b.totalCopies}
                                                </div>
                                            </TableCell>
                                            <TableCell className="text-right space-x-2">
                                                <Button onClick={() => handleEditBook(b)} size="icon" variant="outline" className="rounded-full border-gray-200 hover:bg-black hover:text-white transition-all shadow-sm">
                                                    <Edit3 size={16} />
                                                </Button>
                                                <Button onClick={() => handleDeleteBook(b.id)} size="icon" variant="outline" className="rounded-full border-gray-200 text-red-500 hover:bg-red-500 hover:text-white transition-all shadow-sm">
                                                    <Trash2 size={16} />
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </div>
                    </div>

                    <div className="space-y-4">
                        <h2 className="text-2xl font-bold tracking-tight text-gray-800 flex items-center gap-2">
                            <Plus className="text-black" /> Add Book
                        </h2>
                        <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm p-6 shadow-xl space-y-4">
                            <div className="space-y-1.5">
                                <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Title</label>
                                <Input value={newTitle} onChange={e => setNewTitle(e.target.value)} className="rounded-xl border-gray-200 bg-gray-50 focus-visible:ring-1 focus-visible:ring-black" />
                            </div>
                            <div className="space-y-1.5">
                                <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Author</label>
                                <Input value={newAuthor} onChange={e => setNewAuthor(e.target.value)} className="rounded-xl border-gray-200 bg-gray-50 focus-visible:ring-1 focus-visible:ring-black" />
                            </div>
                            <div className="space-y-1.5">
                                <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">ISBN</label>
                                <Input value={newIsbn} onChange={e => setNewIsbn(e.target.value)} className="rounded-xl border-gray-200 bg-gray-50 focus-visible:ring-1 focus-visible:ring-black" />
                            </div>
                            <div className="space-y-1.5">
                                <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Total Copies</label>
                                <Input type="number" value={newCopies} onChange={e => setNewCopies(e.target.value)} className="rounded-xl border-gray-200 bg-gray-50 focus-visible:ring-1 focus-visible:ring-black" />
                            </div>
                            <Button onClick={handleAddBook} className="w-full rounded-xl bg-black text-white hover:bg-gray-800 transition-all font-bold shadow-md mt-4 hover:scale-[1.02] active:scale-[0.98]">
                                <Plus className="mr-2 h-4 w-4" /> Save Book
                            </Button>
                        </div>
                    </div>
                </section>
            )}

            {/* USERS CONTENT */}
            {activeTab === "users" && (
                <section className="space-y-4 animate-in slide-in-from-bottom-2 fade-in duration-500">
                    <h2 className="text-2xl font-bold tracking-tight text-gray-800 flex items-center gap-2">
                        <Users className="text-black" /> Registered Users
                    </h2>
                    <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden">
                        <Table>
                            <TableHeader className="bg-gray-50/80">
                                <TableRow className="hover:bg-transparent border-b-gray-200">
                                    <TableHead className="font-bold text-gray-700 py-4">ID</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4">Username</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4">Email</TableHead>
                                    <TableHead className="font-bold text-gray-700 py-4">Role</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {users.map(u => (
                                    <TableRow key={u.id} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                                        <TableCell className="font-mono text-sm text-gray-500">{u.id}</TableCell>
                                        <TableCell className="font-bold text-gray-900">{u.username}</TableCell>
                                        <TableCell className="text-gray-600">{u.email}</TableCell>
                                        <TableCell>
                                            <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-bold ${u.role === 'ADMIN' ? 'bg-black text-white' : 'bg-gray-100 text-gray-700 border border-gray-200'}`}>
                                                {u.role}
                                            </span>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </section>
            )}
        </div>
    )
}
