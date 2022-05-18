import bcrypt from 'bcrypt';
import User from '../../modules/user/model/User.js';

export async function createInitialData() {

    try {
        await User.sync({ force: true })
        let password = await bcrypt.hash('123456', 10)

        await User.create({
            name: 'User test 1',
            email: 'teste1@teste.com.br',
            password: password
        })

        await User.create({
            name: 'User test 2',
            email: 'teste2@teste.com.br',
            password: password
        })
    } catch (e) {
        console.error(e)
    }
}