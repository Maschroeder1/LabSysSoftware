export { requestLogin, initializeClasses, retrieveClasses, retrieveEnrollmentDeclaration }

async function requestLogin(user, password) {
    let promise = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        credentials: "include",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
        body: JSON.stringify({ "user": user, "password": password })
        })
    
    let status = promise.status
    let body = await promise.json()

    return {"statusCode": status, "content": body}
}

async function initializeClasses() {
    let promise = await fetch('http://localhost:8080/api/classes', {
        method: 'POST',
        credentials: "include",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            }
        })
    
    let status = promise.status
    let body = await promise.json()

    return {"statusCode": status, "content": body}
}

async function retrieveClasses(code) {
    let promise = await fetch('http://localhost:8080/api/classes/' + code, {
        method: 'GET',
        credentials: "include",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            }
        })
    
    let status = promise.status
    let body = await promise.json()

    return {"statusCode": status, "content": body}
}

async function retrieveEnrollmentDeclaration() {
    let promise = await fetch('http://localhost:8080/api/enrollmentdeclaration', {
        method: 'GET',
        credentials: "include",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            }
        })
    
    let status = promise.status
    let body = await promise.json()

    return {"statusCode": status, "content": body}
}