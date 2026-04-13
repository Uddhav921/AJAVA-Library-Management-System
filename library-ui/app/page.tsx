"use client"

import React, { useEffect, useState } from "react"
import { BGPattern } from "@/components/ui/bg-pattern"
import { AdminDashboard } from "@/components/admin-dashboard"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Library, User as UserIcon, BookOpen, Clock, UserCircle, LogOut, Plus, Trash2, Activity } from "lucide-react"

// Types
type User = { id: number; username: string; email: string; role: string }
type Book = { id: number; title: string; author: string; isbn: string; totalCopies: number; availableCopies: number }
type BorrowRecord = { recordId: number; bookTitle: string; issueDate: string; returnDate: string | null; dueDate: string; fine: number; status: string; finePaid: boolean }

export default function LibraryApp() {
  const [currentUser, setCurrentUser] = useState<User | null>(null)

  return (
    <main className="min-h-screen bg-gray-50/50">
      {currentUser && (
        <header className="relative z-10 border-b border-gray-100 bg-white/80 backdrop-blur-md py-6 px-8 flex justify-between items-center shadow-sm">
          <h1 className="text-3xl font-black tracking-tight flex items-center gap-3">
            <BookOpen className="text-black" /> Library System
          </h1>
          <div className="flex items-center gap-4">
            <div className="px-4 py-2 bg-gray-50 rounded-full border border-gray-200 font-semibold flex items-center gap-2 text-sm">
              <UserCircle size={18} />
              {currentUser.username} {currentUser.role === 'ADMIN' ? '(ADMIN)' : ''}
            </div>
            <Button onClick={() => setCurrentUser(null)} className="rounded-full bg-white border border-gray-200 text-black hover:bg-gray-100 font-bold transition-all shadow-sm">
              <LogOut className="mr-2 h-4 w-4" /> Logout
            </Button>
          </div>
        </header>
      )}

      <div className="max-w-7xl mx-auto p-8 space-y-12">
        {!currentUser ? (
          <AuthScreen onAuth={setCurrentUser} />
        ) : currentUser.role === 'ADMIN' ? (
          <AdminDashboard user={currentUser} />
        ) : (
          <Dashboard user={currentUser} />
        )}
      </div>
    </main>
  )
}

function AuthScreen({ onAuth }: { onAuth: (u: User) => void }) {
  const [isLogin, setIsLogin] = useState(true)
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [email, setEmail] = useState("")
  const [error, setError] = useState("")
  const [isLoading, setIsLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!username || !password || (!isLogin && !email)) {
      setError("Please fill all required fields.")
      return
    }

    setIsLoading(true)
    try {
      const action = isLogin ? "login" : "register"
      const payload = isLogin
        ? { username, password }
        : { username, password, email, role: "MEMBER" }

      const res = await fetch(`http://localhost:8080/api/users/${action}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      })

      if (!res.ok) {
        const errJson = await res.json().catch(() => null)
        throw new Error(errJson?.message || `Failed to ${action}`)
      }

      const data = await res.json()
      onAuth(data)
    } catch (err: any) {
      setError(err.message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex justify-center items-center py-20 animate-in fade-in zoom-in-95 duration-500">
      <Card className="w-full max-w-sm rounded-2xl border border-gray-200 shadow-xl overflow-hidden bg-white/80 backdrop-blur-sm">
        <CardHeader className="pt-8 pb-4">
          <CardTitle className="text-3xl font-black tracking-tight text-center">
            {isLogin ? "Welcome Back" : "Create Account"}
          </CardTitle>
          <p className="text-center text-sm text-gray-500 mt-2">
            {isLogin ? "Enter your credentials to access the library" : "Sign up to start borrowing books"}
          </p>
        </CardHeader>
        <CardContent className="px-8 pb-8 space-y-6">
          {error && (
            <div className="bg-red-50 text-red-600 rounded-lg p-3 text-sm font-medium animate-in fade-in slide-in-from-top-2">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1.5">
              <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Username</label>
              <Input
                value={username} onChange={e => setUsername(e.target.value)}
                className="h-12 rounded-xl bg-gray-50 border-gray-200 focus-visible:ring-1 focus-visible:ring-black focus-visible:border-black transition-all"
                placeholder="johndoe"
              />
            </div>

            {!isLogin && (
              <div className="space-y-1.5 animate-in slide-in-from-top-4 fade-in duration-300">
                <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Email</label>
                <Input
                  type="email"
                  value={email} onChange={e => setEmail(e.target.value)}
                  className="h-12 rounded-xl bg-gray-50 border-gray-200 focus-visible:ring-1 focus-visible:ring-black focus-visible:border-black transition-all"
                  placeholder="john@example.com"
                />
              </div>
            )}

            <div className="space-y-1.5">
              <label className="font-semibold text-xs text-gray-700 uppercase tracking-wider">Password</label>
              <Input
                type="password"
                value={password} onChange={e => setPassword(e.target.value)}
                className="h-12 rounded-xl bg-gray-50 border-gray-200 focus-visible:ring-1 focus-visible:ring-black focus-visible:border-black transition-all"
                placeholder="••••••••"
              />
            </div>

            <Button
              type="submit"
              disabled={isLoading}
              className="w-full h-12 rounded-xl bg-black text-white hover:bg-gray-800 transition-all hover:scale-[1.02] active:scale-[0.98] font-bold shadow-md mt-6"
            >
              {isLoading ? "Processing..." : isLogin ? "Sign In" : "Sign Up"}
            </Button>
          </form>

          <div className="pt-4 text-center">
            <button
              type="button"
              onClick={() => { setIsLogin(!isLogin); setError("") }}
              className="text-sm font-medium text-gray-600 hover:text-black hover:underline transition-all"
            >
              {isLogin ? "Don't have an account? Sign up" : "Already have an account? Sign in"}
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

function Dashboard({ user }: { user: User }) {
  const [availableBooks, setAvailableBooks] = useState<Book[]>([])
  const [myBorrows, setMyBorrows] = useState<BorrowRecord[]>([])
  const [historyBorrows, setHistoryBorrows] = useState<BorrowRecord[]>([])

  const loadData = async () => {
    try {
      const [booksRes, borrowsRes] = await Promise.all([
        fetch("http://localhost:8080/api/books/available"),
        fetch(`http://localhost:8080/api/borrow/history/${user.id}`)
      ])
      if (booksRes.ok) setAvailableBooks(await booksRes.json())
      if (borrowsRes.ok) {
        const allBorrows: BorrowRecord[] = await borrowsRes.json()
        setMyBorrows(allBorrows.filter(b => b.status === 'ISSUED'))
        setHistoryBorrows(allBorrows.filter(b => b.status === 'RETURNED'))
      }
    } catch (e) {
      console.error("Failed to load dashboard data", e)
    }
  }

  useEffect(() => {
    loadData()
    const interval = setInterval(loadData, 5000) // Poll every 5s just in case
    return () => clearInterval(interval)
  }, [user.id])

  const handleBorrow = async (bookId: number) => {
    try {
      const res = await fetch(`http://localhost:8080/api/borrow/issue?userId=${user.id}&bookId=${bookId}`, {
        method: "POST"
      })
      if (res.ok) loadData()
      else alert((await res.json()).message || "Failed to borrow book")
    } catch (e) {
      console.error("Borrow error", e)
    }
  }

  const handleReturn = async (recordId: number) => {
    try {
      const res = await fetch(`http://localhost:8080/api/borrow/return/${recordId}`, {
        method: "PUT"
      })
      if (res.ok) loadData()
      else alert((await res.json()).message || "Failed to return book")
    } catch (e) {
      console.error("Return error", e)
    }
  }

  const handlePayFine = async (recordId: number) => {
    try {
      // 1. Create order
      const orderRes = await fetch(`http://localhost:8080/api/payment/create-order?recordId=${recordId}`, { method: "POST" })
      if (!orderRes.ok) {
        alert("Failed to initialize payment")
        return
      }
      const orderData = await orderRes.json()

      // 2. Open Razorpay Checkout overlay
      const options = {
        key: process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || "rzp_test_YourKeyIdHere",
        amount: orderData.amount,
        currency: "INR",
        name: "Library System",
        description: "Late Return Fine",
        order_id: orderData.orderId,
        handler: async function (response: any) {
          // 3. Verify on backend
          const verifyRes = await fetch(`http://localhost:8080/api/payment/verify?recordId=${recordId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature
            })
          })

          if (verifyRes.ok) {
            alert("Payment Successful! Fine cleared.");
            loadData();
          } else {
            alert("Payment signature verification failed!");
          }
        },
        theme: { color: "#000000" }
      }

      const rzp = new (window as any).Razorpay(options)
      rzp.open()
    } catch (e) {
      console.error("Payment error", e)
    }
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 pt-8 px-4 animate-in fade-in duration-500 max-w-7xl mx-auto">
      {/* LEFT PANE: AVAILABLE BOOKS */}
      <section className="space-y-4">
        <h2 className="text-2xl font-bold tracking-tight flex items-center gap-2 text-gray-800">
          <BookOpen size={24} className="text-black" /> Available Books
        </h2>
        <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden transition-all">
          <Table>
            <TableHeader className="bg-gray-50/80">
              <TableRow className="hover:bg-transparent border-b-gray-200">
                <TableHead className="font-bold text-gray-700 py-4">Title / Author</TableHead>
                <TableHead className="font-bold text-gray-700 py-4 text-center">Stock</TableHead>
                <TableHead className="font-bold text-gray-700 py-4 text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {availableBooks.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={3} className="text-center py-10 font-medium text-gray-400">
                    No books currently available.
                  </TableCell>
                </TableRow>
              ) : (
                availableBooks.map(book => (
                  <TableRow key={book.id} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                    <TableCell>
                      <p className="font-bold text-gray-900">{book.title}</p>
                      <p className="text-sm text-gray-500">{book.author}</p>
                    </TableCell>
                    <TableCell className="text-center font-semibold text-gray-700">{book.availableCopies}</TableCell>
                    <TableCell className="text-right">
                      <Button
                        onClick={() => handleBorrow(book.id)}
                        className="rounded-full bg-black text-white hover:bg-gray-800 transition-all hover:-translate-y-[1px] font-semibold text-sm shadow-md"
                      >
                        Borrow
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </section>

      {/* RIGHT PANE: MY BORROWED BOOKS */}
      <section className="space-y-4">
        <h2 className="text-2xl font-bold tracking-tight flex items-center gap-2 text-gray-800">
          <Clock size={24} className="text-black" /> My Borrowed Books
        </h2>
        <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden transition-all">
          <Table>
            <TableHeader className="bg-gray-50/80">
              <TableRow className="hover:bg-transparent border-b-gray-200">
                <TableHead className="font-bold text-gray-700 py-4">Book</TableHead>
                <TableHead className="font-bold text-gray-700 py-4">Due Date</TableHead>
                <TableHead className="font-bold text-gray-700 py-4 text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {myBorrows.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={3} className="text-center py-10 font-medium text-gray-400">
                    You have no active borrows.
                  </TableCell>
                </TableRow>
              ) : (
                myBorrows.map(record => (
                  <TableRow key={record.recordId} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                    <TableCell>
                      <p className="font-bold text-gray-900">{record.bookTitle}</p>
                      {record.fine > 0 && (
                        <p className="text-red-500 font-semibold text-xs mt-1">Fine Due: ₹{record.fine}</p>
                      )}
                    </TableCell>
                    <TableCell className="font-medium text-sm text-gray-600">{record.dueDate}</TableCell>
                    <TableCell className="text-right">
                      {record.fine > 0 && !record.finePaid ? (
                        <Button
                          onClick={() => handlePayFine(record.recordId)}
                          className="rounded-full bg-black text-white hover:bg-gray-800 transition-all hover:-translate-y-[1px] font-semibold text-sm shadow-sm"
                        >
                          Pay Fine
                        </Button>
                      ) : (
                        <Button
                          onClick={() => handleReturn(record.recordId)}
                          variant="outline"
                          className="rounded-full border border-gray-300 text-gray-700 hover:bg-black hover:text-white hover:border-black transition-all hover:-translate-y-[1px] font-semibold text-sm shadow-sm"
                        >
                          Return
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </section>

      {/* BOTTOM PANE: BORROW HISTORY */}
      <section className="space-y-4 lg:col-span-2 pt-6">
        <h2 className="text-2xl font-bold tracking-tight flex items-center gap-2 text-gray-800">
          <BookOpen size={24} className="text-black" /> Borrow History
        </h2>
        <div className="rounded-2xl border border-gray-200 bg-white/80 backdrop-blur-sm shadow-xl overflow-hidden transition-all">
          <Table>
            <TableHeader className="bg-gray-50/80">
              <TableRow className="hover:bg-transparent border-b-gray-200">
                <TableHead className="font-bold text-gray-700 py-4">Title</TableHead>
                <TableHead className="font-bold text-gray-700 py-4">Borrowed</TableHead>
                <TableHead className="font-bold text-gray-700 py-4">Returned</TableHead>
                <TableHead className="font-bold text-gray-700 py-4 text-right">Fine</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {historyBorrows.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-10 font-medium text-gray-400">
                    No past borrow history found.
                  </TableCell>
                </TableRow>
              ) : (
                historyBorrows.map(record => (
                  <TableRow key={record.recordId} className="border-b border-gray-100 hover:bg-gray-50/50 transition-colors">
                    <TableCell>
                      <p className="font-bold text-gray-900">{record.bookTitle}</p>
                    </TableCell>
                    <TableCell className="font-medium text-sm text-gray-600">{record.issueDate}</TableCell>
                    <TableCell className="font-medium text-sm text-gray-600">{record.returnDate}</TableCell>
                    <TableCell className="text-right">
                      {record.fine === 0 ? (
                        <span className="font-bold text-gray-900">₹0</span>
                      ) : record.finePaid ? (
                        <span className="font-bold text-green-600">₹{record.fine} (PAID)</span>
                      ) : (
                        <div className="flex flex-col items-end gap-1">
                          <span className="font-bold text-red-500">₹{record.fine} (DUE)</span>
                          <Button onClick={() => handlePayFine(record.recordId)} size="sm" className="h-7 text-xs rounded-full bg-black text-white hover:bg-gray-800 transition-all font-semibold shadow-sm">
                            Pay Now
                          </Button>
                        </div>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </section>
    </div>
  )
}
