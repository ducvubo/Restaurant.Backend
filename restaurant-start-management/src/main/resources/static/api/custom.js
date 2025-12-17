// Custom Swagger UI Script - Restaurant Management
(function() {
    'use strict';
    console.log('🚀 ========================================');
    console.log('🚀 Custom JS file is loading...');
    console.log('🚀 Timestamp:', new Date().toISOString());
    console.log('🚀 Current URL:', window.location.href);
    console.log('🚀 Document ready state:', document.readyState);
    console.log('✅ Custom JS loaded for Restaurant Management');
    console.log('📍 Swagger UI object:', typeof window.ui !== 'undefined' ? 'exists' : 'not found');
    console.log('🚀 ========================================');
})();

const binder = setInterval(() => {
    try {
        const logoLink = document.querySelector("#swagger-ui a.link");
        const logoImg = document.querySelector("#swagger-ui a.link > svg");
        if (logoImg) {
            logoImg?.remove();
            const img = document.createElement('img')
            img.setAttribute("src", "/api/logo-white.svg")
            logoLink.append(img);
            clearInterval(binder);
        }
    }
    catch { }
}, 100)

document.addEventListener('DOMContentLoaded', function () {

    let link = document.querySelector("link[rel*='icon']") || document.createElement('link');
    if (link.parentNode) {
        document.head.removeChild(link);
    }
    link = document.querySelector("link[rel*='icon']") || document.createElement('link');
    if (link.parentNode) {
        document.head.removeChild(link);
    }
    link = document.createElement('link');
    link.type = 'image/x-icon';
    link.rel = 'shortcut icon';
    link.href = '/api/favicon.ico';
    document.getElementsByTagName('head')[0].appendChild(link);

    let title = document.querySelector("title") || document.createElement('title');
    if (title.parentNode) {
        document.head.removeChild(title);
    }

    title = document.querySelector("title") || document.createElement('title');
    title.text = "RESTAURANT - HỆ THỐNG QUẢN LÝ NHÀ HÀNG";
    document.getElementsByTagName('head')[0].appendChild(title);


    // Inject overlay + login modal
    const customLoginModal = `
        <div id="custom-login-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background-color:rgba(0,0,0,0.5); z-index:9998;"></div>
        <div id="custom-login" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); width:400px; background:#fff; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.3); padding:30px; z-index:9999;">
            <h2 style="margin-bottom:20px; text-align:center;">ĐĂNG NHẬP</h2>
            <div style="margin-bottom:15px;">
                <label>Tên đăng nhập</label>
                <input id="username" placeholder="Nhập tên đăng nhập" value="admin" style="width:100%; padding:10px; margin-top:5px; border:1px solid #ccc; border-radius:4px;">
            </div>
            <div style="margin-bottom:15px;">
                <label>Mật khẩu</label>
                <input id="password" type="password" placeholder="Nhập mật khẩu" value="abc@123" style="width:100%; padding:10px; margin-top:5px; border:1px solid #ccc; border-radius:4px;">
            </div>
            <div style="text-align:center;">
                <button id="loginBtn" style="padding:10px 20px; background:#1eb181; color:white; border:none; border-radius:4px; margin-right:10px;">Đăng nhập</button>
                <button id="closeBtn" style="padding:10px 20px; background:#ccc; color:black; border:none; border-radius:4px;">Đóng</button>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', customLoginModal);

    function showLoginModal() {
        document.getElementById('custom-login').style.display = 'block';
        document.getElementById('custom-login-overlay').style.display = 'block';
        document.getElementById('username').focus();
    }
    async function login() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const now = new Date();

        const pad = (num, size) => String(num).padStart(size, '0');

        const year = now.getFullYear();
        const month = pad(now.getMonth() + 1, 2);
        const day = pad(now.getDate(), 2);
        const hours = pad(now.getHours(), 2);
        const minutes = pad(now.getMinutes(), 2);
        const seconds = pad(now.getSeconds(), 2);
        const milliseconds = pad(now.getMilliseconds(), 3);
        const clientId = `${year}${month}${day}${hours}${minutes}${seconds}${milliseconds}`;
        try {
            const res = await fetch('/api/management/auth/login', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'CLIENT_ID': clientId
                 },
                credentials: 'omit',
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) {
                const errorData = await res.json();
                throw new Error(errorData.message || "Đăng nhập không thành công");
            }

            const data = await res.json();
            console.log('Login response:', data);
            
            // Lấy token từ data.result.accessToken (theo cấu trúc response mới)
            const accessToken = data.result?.accessToken || data.data?.accessToken;
            const refreshToken = data.result?.refreshToken || data.data?.refreshToken;
            
            if (accessToken) {
                // Lưu token và clientId vào localStorage
                localStorage.setItem('swagger_access_token', accessToken);
                localStorage.setItem('swagger_refresh_token', refreshToken);
                localStorage.setItem('swagger_client_id', clientId);
                
                console.log('✅ Token và CLIENT_ID đã được lưu:', {
                    token: accessToken.substring(0, 20) + '...',
                    clientId: clientId
                });
                
                // Set authorization cho Swagger UI
                if (window.ui && window.ui.preauthorizeApiKey) {
                    window.ui.preauthorizeApiKey("Bearer", "Bearer " + accessToken);
                }
                
                // Cập nhật request interceptor để tự động thêm token và CLIENT_ID
                updateRequestInterceptor(accessToken, clientId);
                
                hideLoginModal();
                alert('Đăng nhập thành công! Token đã được lưu và sẽ tự động sử dụng cho các API.');
            }
            else {
                alert(data.message || "Đăng nhập không thành công - Không tìm thấy token");
            }
        } catch (err) {
            console.error('Login error:', err);
            alert(err.message || err);
        }
    }
    
    // Hàm để cập nhật request interceptor
    function updateRequestInterceptor(accessToken, clientId) {
        if (window.ui && window.ui.getConfigs) {
            window.ui.getConfigs().requestInterceptor = (req) => {
                // Thêm Authorization header nếu chưa có
                if (accessToken && !req.headers["Authorization"]) {
                    req.headers["Authorization"] = "Bearer " + accessToken;
                }
                // Thêm CLIENT_ID header nếu chưa có
                if (clientId && !req.headers["CLIENT_ID"]) {
                    req.headers["CLIENT_ID"] = clientId;
                }
                return req;
            };
            console.log('✅ Request interceptor đã được cập nhật');
        } else {
            // Nếu window.ui chưa sẵn sàng, thử lại sau
            setTimeout(() => updateRequestInterceptor(accessToken, clientId), 300);
        }
    }
    
    // Load token và clientId từ localStorage khi page load
    function loadSavedCredentials() {
        const savedToken = localStorage.getItem('swagger_access_token');
        const savedClientId = localStorage.getItem('swagger_client_id');
        
        if (savedToken && savedClientId) {
            console.log('✅ Đã tìm thấy token và CLIENT_ID đã lưu, đang áp dụng...');
            
            // Set authorization cho Swagger UI
            if (window.ui && window.ui.preauthorizeApiKey) {
                window.ui.preauthorizeApiKey("Bearer", "Bearer " + savedToken);
            }
            
            // Cập nhật request interceptor
            updateRequestInterceptor(savedToken, savedClientId);
        }
    }
    
    // Gọi loadSavedCredentials khi Swagger UI sẵn sàng
    const loadCredentialsInterval = setInterval(() => {
        if (window.ui && window.ui.getConfigs) {
            loadSavedCredentials();
            clearInterval(loadCredentialsInterval);
        }
    }, 300);
    function hideLoginModal() {
        document.getElementById('custom-login').style.display = 'none';
        document.getElementById('custom-login-overlay').style.display = 'none';
    }

    document.getElementById('loginBtn').addEventListener('click', login);
    document.getElementById('username').addEventListener('keypress', async function (e) {
        if (e.key === 'Enter') {
            await login();
        }
    });
    document.getElementById('password').addEventListener('keypress', async function (e) {
        if (e.key === 'Enter') {
            await login();
        }
    });

    document.getElementById('closeBtn').addEventListener('click', hideLoginModal);
    document.getElementById('custom-login-overlay').addEventListener('click', hideLoginModal);

    function tryAddLoginButton(retryCount = 0) {
        // Kiểm tra xem button đã tồn tại chưa
        if (document.getElementById('openLoginForm')) {
            return; // Button đã tồn tại, không cần thêm nữa
        }

        // Tìm topbar-wrapper - đây là nơi tốt nhất để thêm button
        let targetElement = document.querySelector('.topbar-wrapper');
        
        // Nếu không tìm thấy, thử các selector khác
        if (!targetElement) {
            targetElement = document.querySelector('.download-url-wrapper') ||
                           document.querySelector('.swagger-ui .topbar-wrapper') ||
                           document.querySelector('#swagger-ui .topbar-wrapper');
        }

        // Nếu vẫn không tìm thấy, thử tìm trong topbar
        if (!targetElement) {
            const topbar = document.querySelector('.topbar');
            if (topbar) {
                targetElement = topbar.querySelector('.topbar-wrapper') || 
                               topbar.querySelector('.wrapper') ||
                               topbar;
            }
        }

        if (targetElement) {
            const loginButton = document.createElement("button");
            loginButton.innerText = "Đăng nhập";
            loginButton.type = "button";
            loginButton.id = "openLoginForm";
            loginButton.style.margin = "0px 10px";
            loginButton.style.padding = "8px 16px";
            loginButton.style.background = "#1eb181";
            loginButton.style.color = "#fff";
            loginButton.style.border = "none";
            loginButton.style.borderRadius = "4px";
            loginButton.style.cursor = "pointer";
            loginButton.style.fontSize = "14px";
            loginButton.style.fontWeight = "bold";
            loginButton.style.display = "inline-block";
            loginButton.style.verticalAlign = "middle";

            loginButton.addEventListener("click", function(e) {
                e.preventDefault();
                e.stopPropagation();
                showLoginModal();
            });

            // Thử thêm vào cuối topbar-wrapper
            try {
                targetElement.appendChild(loginButton);
                console.log('✅ Login button added to topbar-wrapper');
            } catch (e) {
                // Nếu không được, thử insertBefore
                try {
                    targetElement.insertBefore(loginButton, targetElement.firstChild);
                    console.log('✅ Login button prepended to topbar-wrapper');
                } catch (e2) {
                    // Nếu cả hai cách đều fail, thêm vào body như fallback
                    loginButton.style.position = "fixed";
                    loginButton.style.top = "10px";
                    loginButton.style.right = "10px";
                    loginButton.style.zIndex = "9999";
                    document.body.appendChild(loginButton);
                    console.log('✅ Login button added to body as fallback');
                }
            }
        } else {
            // Nếu không tìm thấy target element, retry
            if (retryCount < 100) {
                setTimeout(() => tryAddLoginButton(retryCount + 1), 200);
            } else {
                console.warn('⚠️ Could not find target element for login button after 100 retries');
                // Thêm button vào body như fallback cuối cùng
                if (!document.getElementById('openLoginForm')) {
                    const loginButton = document.createElement("button");
                    loginButton.innerText = "Đăng nhập";
                    loginButton.type = "button";
                    loginButton.id = "openLoginForm";
                    loginButton.style.position = "fixed";
                    loginButton.style.top = "10px";
                    loginButton.style.right = "10px";
                    loginButton.style.padding = "8px 16px";
                    loginButton.style.background = "#1eb181";
                    loginButton.style.color = "#fff";
                    loginButton.style.border = "none";
                    loginButton.style.borderRadius = "4px";
                    loginButton.style.cursor = "pointer";
                    loginButton.style.zIndex = "9999";
                    loginButton.style.fontSize = "14px";
                    loginButton.style.fontWeight = "bold";
                    loginButton.addEventListener("click", showLoginModal);
                    document.body.appendChild(loginButton);
                    console.log('✅ Login button added to body as final fallback');
                }
            }
        }

        // Ẩn scheme selector nếu có
        const swaggerContainer = document.querySelector('.scheme-container');
        if (swaggerContainer) {
            swaggerContainer.style.display = 'none';
        }
    }

    // Sử dụng MutationObserver để theo dõi khi Swagger UI render
    const observer = new MutationObserver((mutations, obs) => {
        const topbarWrapper = document.querySelector('.topbar-wrapper');
        if (topbarWrapper && !document.getElementById('openLoginForm')) {
            tryAddLoginButton();
            // Nếu đã tìm thấy và thêm button, có thể dừng observer sau một chút
            setTimeout(() => {
                if (document.getElementById('openLoginForm')) {
                    obs.disconnect();
                    console.log('✅ Observer disconnected - login button found');
                }
            }, 1000);
        }
    });

    // Bắt đầu observe
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });

    // Thử ngay lập tức và sau đó retry
    tryAddLoginButton();
    setTimeout(() => tryAddLoginButton(), 500);
    setTimeout(() => tryAddLoginButton(), 1000);
    setTimeout(() => tryAddLoginButton(), 2000);
});


function bindClientIdInterceptor() {
    if (window.ui && window.ui.getConfigs) {
        // Kiểm tra xem đã có token và CLIENT_ID đã lưu chưa
        const savedToken = localStorage.getItem('swagger_access_token');
        const savedClientId = localStorage.getItem('swagger_client_id');
        
        // Nếu đã có token và CLIENT_ID, sử dụng chúng
        if (savedToken && savedClientId) {
            const oldInterceptor = window.ui.getConfigs().requestInterceptor;
            window.ui.getConfigs().requestInterceptor = (req) => {
                // Thêm Authorization header nếu chưa có
                if (!req.headers["Authorization"]) {
                    req.headers["Authorization"] = "Bearer " + savedToken;
                }
                // Thêm CLIENT_ID header nếu chưa có
                if (!req.headers["CLIENT_ID"]) {
                    req.headers["CLIENT_ID"] = savedClientId;
                }
                if (oldInterceptor) {
                    req = oldInterceptor(req);
                }
                return req;
            };
            console.log("✅ Request interceptor đã được cấu hình với token và CLIENT_ID đã lưu");
        } else {
            // Nếu chưa có, tạo CLIENT_ID mới (fallback)
            const now = new Date();
            const pad = (num, size) => String(num).padStart(size, '0');
            const clientId = `${now.getFullYear()}${pad(now.getMonth() + 1, 2)}${pad(now.getDate(), 2)}${pad(now.getHours(), 2)}${pad(now.getMinutes(), 2)}${pad(now.getSeconds(), 2)}${pad(now.getMilliseconds(), 3)}`;

            const oldInterceptor = window.ui.getConfigs().requestInterceptor;
            window.ui.getConfigs().requestInterceptor = (req) => {
                // Chỉ thêm CLIENT_ID nếu chưa có (không thêm Authorization vì chưa login)
                if (!req.headers["CLIENT_ID"]) {
                    req.headers["CLIENT_ID"] = clientId;
                }
                if (oldInterceptor) {
                    req = oldInterceptor(req);
                }
                return req;
            };
            console.log("✅ CLIENT_ID interceptor attached (fallback):", clientId);
        }
    } else {
        setTimeout(bindClientIdInterceptor, 300); // thử lại sau 0.3s
    }
}

bindClientIdInterceptor();

