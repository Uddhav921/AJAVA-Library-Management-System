<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Report | Library Management System</title>
    <style>
        /* ── Root Variables ───────────────────────── */
        :root {
            --primary: #1a237e;
            --accent:  #ff6f00;
            --bg:      #f5f7fa;
            --card:    #ffffff;
            --danger:  #c62828;
            --success: #2e7d32;
            --text:    #212121;
            --muted:   #757575;
            --radius:  8px;
            --shadow:  0 2px 12px rgba(0,0,0,0.08);
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
               background: var(--bg); color: var(--text); }

        /* ── Header ─────────────────────────────── */
        header {
            background: var(--primary);
            color: #fff;
            padding: 20px 40px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        header h1 { font-size: 1.4rem; }
        header span { font-size: 0.85rem; opacity: 0.8; }

        /* ── Main container ──────────────────────── */
        main { max-width: 1100px; margin: 30px auto; padding: 0 20px; }

        /* ── Stats grid ──────────────────────────── */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
            gap: 16px;
            margin-bottom: 36px;
        }
        .stat-card {
            background: var(--card);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            padding: 20px;
            text-align: center;
        }
        .stat-card .value {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary);
        }
        .stat-card.danger .value  { color: var(--danger);  }
        .stat-card.success .value { color: var(--success); }
        .stat-card .label {
            font-size: 0.78rem;
            color: var(--muted);
            margin-top: 6px;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }

        /* ── Section heading ─────────────────────── */
        h2 {
            font-size: 1.1rem;
            margin-bottom: 14px;
            color: var(--primary);
            border-left: 4px solid var(--accent);
            padding-left: 10px;
        }

        /* ── Table ───────────────────────────────── */
        .table-wrap { overflow-x: auto; }
        table {
            width: 100%;
            border-collapse: collapse;
            background: var(--card);
            border-radius: var(--radius);
            overflow: hidden;
            box-shadow: var(--shadow);
        }
        thead { background: var(--primary); color: #fff; }
        th, td { padding: 12px 16px; text-align: left; font-size: 0.88rem; }
        tbody tr:nth-child(even) { background: #f9fafb; }
        tbody tr:hover { background: #e8eaf6; }
        .badge {
            display: inline-block;
            padding: 2px 10px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
        }
        .badge-danger  { background: #ffebee; color: var(--danger);  }
        .badge-warning { background: #fff8e1; color: #e65100; }
        .badge-success { background: #e8f5e9; color: var(--success); }

        /* ── Empty state ─────────────────────────── */
        .empty { text-align: center; padding: 40px; color: var(--muted); }

        /* ── Footer ──────────────────────────────── */
        footer {
            text-align: center;
            padding: 20px;
            font-size: 0.78rem;
            color: var(--muted);
            margin-top: 40px;
        }

        /* ── CO Badge ────────────────────────────── */
        .co-tag {
            background: var(--primary);
            color: #fff;
            font-size: 0.7rem;
            padding: 2px 7px;
            border-radius: 4px;
            margin-left: 8px;
            vertical-align: middle;
        }
    </style>
</head>
<body>

<header>
    <div>
        <h1>📚 Library Management System — Admin Report</h1>
        <span>JSP Admin Dashboard &nbsp;<span class="co-tag">CO1</span>&nbsp;<span class="co-tag">CO4</span></span>
    </div>
    <span>Generated: <c:out value="${generatedAt}"/></span>
</header>

<main>

    <%-- =========================================================
         SECTION 1: Library Statistics Cards
         JSTL <c:out> and EL expressions demonstrated here.
         ========================================================= --%>
    <h2>Library Overview</h2>
    <div class="stats-grid">

        <div class="stat-card">
            <div class="value"><c:out value="${stats.totalBooks}"/></div>
            <div class="label">📖 Total Books</div>
        </div>

        <div class="stat-card">
            <div class="value"><c:out value="${stats.totalUsers}"/></div>
            <div class="label">👤 Registered Members</div>
        </div>

        <div class="stat-card">
            <div class="value"><c:out value="${stats.activeBorrows}"/></div>
            <div class="label">📤 Currently Issued</div>
        </div>

        <div class="stat-card danger">
            <div class="value"><c:out value="${stats.overdueCount}"/></div>
            <div class="label">⚠️ Overdue Books</div>
        </div>

        <div class="stat-card success">
            <div class="value"><c:out value="${stats.returnedRecords}"/></div>
            <div class="label">✅ Returned Books</div>
        </div>

        <div class="stat-card success">
            <div class="value">₹<fmt:formatNumber value="${stats.totalFinesCollected}" maxFractionDigits="0"/></div>
            <div class="label">💰 Fines Collected</div>
        </div>

        <div class="stat-card danger">
            <div class="value">₹<fmt:formatNumber value="${stats.totalFinesPending}" maxFractionDigits="0"/></div>
            <div class="label">⏳ Fines Pending</div>
        </div>

    </div>

    <%-- =========================================================
         SECTION 2: Overdue Records Table
         JSTL <c:forEach> + <c:choose> + <c:when> demonstrated.
         ========================================================= --%>
    <h2>Overdue Borrow Records <span class="co-tag">JSTL forEach</span></h2>
    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty overdueList}">
                <div class="empty">
                    <p>🎉 No overdue records! All books returned on time.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Record ID</th>
                            <th>Member</th>
                            <th>Book Title</th>
                            <th>ISBN</th>
                            <th>Due Date</th>
                            <th>Fine (INR)</th>
                            <th>Fine Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- JSTL forEach loop (CO1 - JSTL) --%>
                        <c:forEach var="record" items="${overdueList}" varStatus="loop">
                            <tr>
                                <td><c:out value="${loop.count}"/></td>
                                <td>#<c:out value="${record.recordId}"/></td>
                                <td><c:out value="${record.username}"/></td>
                                <td><c:out value="${record.bookTitle}"/></td>
                                <td><c:out value="${record.isbn}"/></td>
                                <td><c:out value="${record.dueDate}"/></td>
                                <td>
                                    <fmt:formatNumber value="${record.fine}" type="number"
                                                      minFractionDigits="2" maxFractionDigits="2"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${record.finePaid}">
                                            <span class="badge badge-success">Paid</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">Unpaid</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- =========================================================
         SECTION 3: Course Outcome Map
         ========================================================= --%>
    <br/>
    <h2>Course Outcome Coverage (This Page)</h2>
    <div class="table-wrap">
        <table>
            <thead>
                <tr><th>CO</th><th>Outcome</th><th>How This Page Demonstrates It</th></tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>CO1</strong></td>
                    <td>Servlet &amp; JSP API</td>
                    <td>
                        This JSP page rendered via <code>InternalResourceViewResolver</code>;
                        JSTL <code>c:forEach</code>, <code>c:choose</code>, <code>fmt:formatNumber</code>;
                        <code>RequestLoggingFilter</code> (Servlet Filter) logged this request.
                    </td>
                </tr>
                <tr>
                    <td><strong>CO2</strong></td>
                    <td>OOP, JDBC, Multithreading</td>
                    <td>
                        <code>LibraryStatsDto</code> (OOP/POJO); JPA queries (Hibernate PreparedStatement under the hood);
                        <code>FineNotificationService.@Async</code> on <code>library-async-pool</code> thread.
                    </td>
                </tr>
                <tr>
                    <td><strong>CO3</strong></td>
                    <td>Java Frameworks</td>
                    <td>Spring Boot 3.3.4, Hibernate ORM, Lombok, Spring Security, Razorpay SDK.</td>
                </tr>
                <tr>
                    <td><strong>CO4</strong></td>
                    <td>Relational Database</td>
                    <td>MySQL 8 — stats aggregated from <code>users</code>, <code>books</code>, <code>borrow_records</code> via Spring Data JPA.</td>
                </tr>
                <tr>
                    <td><strong>CO5</strong></td>
                    <td>Full-Stack Web App</td>
                    <td>Spring Boot REST backend + Next.js 16 frontend + Razorpay payment integration.</td>
                </tr>
            </tbody>
        </table>
    </div>

</main>

<footer>
    Library Management System &copy; 2026 &bull; JSP Admin Report &bull;
    CO1 &mdash; Servlet &amp; JSP API
</footer>

</body>
</html>
