(async function initializeGrantTrackSession() {
    try {
        const response = await fetch("/api/auth/me");
        if (!response.ok) {
            window.location.replace("/login.html");
            return;
        }

        const user = await response.json();
        window.grantTrackUser = user;
        document.body.dataset.role = user.role;

        // Show/hide data-admin-only elements
        document.querySelectorAll("[data-admin-only]").forEach(el => {
            el.hidden = user.role !== "ADMIN";
        });

        // Nav visibility
        document.querySelectorAll(".nav-item").forEach(link => {
            const path = new URL(link.href, window.location.origin).pathname;

            // Admin-only nav items — hide for faculty
            if (user.role !== "ADMIN" && [
                "/index.html", "/", "/faculty.html", "/stages.html"
            ].includes(path)) {
                link.hidden = true;
            }

            // Faculty-only nav items — hide for admin
            if (user.role === "ADMIN" && [
                "/new-application.html", "/expenditures.html"
            ].includes(path)) {
                link.hidden = true;
            }

            // Rename Applications link for faculty
            if (user.role === "FACULTY" && path === "/applications.html") {
                link.querySelector("span")
                    ? link.innerHTML = "<span>▤</span> My Applications"
                    : link.textContent = "My Applications";
            }
        });

        // For faculty: inject nav links on pages that don't already have them hardcoded
        if (user.role === "FACULTY") {
            const nav = document.querySelector(".sidebar nav");
            if (nav) {
                if (!nav.querySelector('[href="/new-application.html"]')) {
                    const link = document.createElement("a");
                    link.href = "/new-application.html";
                    link.className = "nav-item";
                    link.innerHTML = "<span>＋</span> New Application";
                    nav.appendChild(link);
                }
                if (!nav.querySelector('[href="/expenditures.html"]')) {
                    const expLink = document.createElement("a");
                    expLink.href = "/expenditures.html";
                    expLink.className = "nav-item";
                    expLink.innerHTML = "<span>₹</span> Expenditures";
                    nav.appendChild(expLink);
                }
            }

            const heading = document.querySelector("h1");
            if (heading && window.location.pathname.endsWith("applications.html")) {
                heading.textContent = "My Applications";
            }
            const sectionHeading = document.querySelector(".section-header h2");
            if (sectionHeading && window.location.pathname.endsWith("applications.html")) {
                sectionHeading.textContent = "My Applications";
            }
        }

        // Update profile bar
        const profile = document.querySelector(".profile");
        if (profile) {
            const avatar = profile.querySelector(".profile-avatar");
            const name = profile.querySelector(".profile-info strong");
            const description = profile.querySelector(".profile-info span");
            if (avatar) avatar.textContent = (user.name || "G").charAt(0).toUpperCase();
            if (name) name.textContent = user.name;
            if (description) description.textContent =
                user.role === "ADMIN" ? "Administrator" : "Faculty";

            if (!profile.querySelector(".logout-button")) {
                const logout = document.createElement("button");
                logout.type = "button";
                logout.className = "logout-button";
                logout.textContent = "Log out";
                logout.addEventListener("click", async () => {
                    await fetch("/api/auth/logout", { method: "POST" });
                    window.location.replace("/login.html");
                });
                profile.appendChild(logout);
            }
        }
    } catch (error) {
        window.location.replace("/login.html");
    }
})();
