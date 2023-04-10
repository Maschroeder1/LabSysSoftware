import {useState} from "react";
import {requestLogin} from "./requests";

export const Login = ({loginCallback}) => {

    const [login, setLogin] = useState("")
    const [password, setPassword] = useState("")

    return <div>
        <div className="login-wrapper">
        <div><img src={require('./ufrgs_logo.jpg')} alt="UFRGS"></img></div>
        <div className="login-input">Login: <input onChange={(e) => setLogin(e.target.value)}/></div>
        <div className="login-input">Senha: <input onChange={(e) => setPassword(e.target.value)} type="password"/></div>
        <button onClick={async () => {
            if (false) {
                loginCallback(true)
            } else {
            const loginResponse = await requestLogin(login, password)
            
            switch (loginResponse.statusCode) {
                case 200:
                    loginCallback(true)
                    break
                case 400: // input mal formatado, user e/ou senha vazio, ou captcha
                case 401: // user/senha invalido
                case 504: // erro de network com a ufrgs
                case 500: // erro pegando cookie da resposta da ufrgs
                default: // nao deve ser possivel
            }
            }
        }
        }>Enviar</button>

        </div>
    </div>
}