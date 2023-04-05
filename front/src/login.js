import {useState} from "react";

export const Login = ({loginCallback}) => {

    const [login, setLogin] = useState("")
    const [password, setPassword] = useState("")
console.log({login, password})
    return <div>
        <div className="login-wrapper">

        <div>Login: <input onChange={(e) => setLogin(e.target.value)}/></div>
        <div>Senha: <input onChange={(e) => setPassword(e.target.value)} type="password"/></div>
            <button onClick={() => {
            // TODO: chamada de api
                console.log({login, password})
                loginCallback(true) // TODO: se logar, só dale true aqui

                /*
                if(!loginSuccess) {
                 return;
                }
                loginCallback(true)
                * */
            }
            }>Logar!</button>
        </div>
    </div>
}