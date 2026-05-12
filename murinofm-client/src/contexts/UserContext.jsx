import { createContext, useContext, useState, useEffect } from 'react';

const UserContext = createContext(null);

export function UserProvider({ children }) {
  const [activeUser, setActiveUser] = useState(() => {
    const saved = localStorage.getItem('activeUser');
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    if (activeUser) {
      localStorage.setItem('activeUser', JSON.stringify(activeUser));
    } else {
      localStorage.removeItem('activeUser');
    }
  }, [activeUser]);

  return (
    <UserContext.Provider value={{ activeUser, setActiveUser }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  return useContext(UserContext);
}