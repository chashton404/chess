import { useEffect, useRef, useState } from 'react';
import {
  createGame,
  joinGame,
  listGames,
  loginUser,
  logoutUser,
  registerUser,
} from './api';
import {
  FILES,
  PROMOTION_OPTIONS,
  boardCols,
  boardRows,
  getPieceSymbol,
  isPromotionMove,
  squareLabel,
  statusFromGame,
} from './chess';

const SESSION_KEY = 'castleline-session';

function readStoredSession() {
  try {
    const raw = window.localStorage.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function parseSocketUrl() {
  const url = new URL(window.location.href);
  url.protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
  url.pathname = '/ws';
  url.search = '';
  url.hash = '';
  return url.toString();
}

function normalizeMessage(data) {
  switch (data.serverMessageType) {
    case 'LOAD_GAME':
      return {
        type: 'LOAD_GAME',
        game: data.game,
        playerColor: data.playerColor,
      };
    case 'NOTIFICATION':
      return {
        type: 'NOTIFICATION',
        text: data.message,
      };
    case 'ERROR':
      return {
        type: 'ERROR',
        text: data.errorMessage,
      };
    default:
      return null;
  }
}

function emptyForms() {
  return {
    login: { username: '', password: '' },
    register: { username: '', password: '', email: '' },
    create: { gameName: '' },
  };
}

function App() {
  const [session, setSession] = useState(readStoredSession);
  const [forms, setForms] = useState(emptyForms);
  const [games, setGames] = useState([]);
  const [activeGame, setActiveGame] = useState(null);
  const [selectedSquare, setSelectedSquare] = useState(null);
  const [banner, setBanner] = useState('');
  const [error, setError] = useState('');
  const [busyAction, setBusyAction] = useState('');
  const socketRef = useRef(null);

  useEffect(() => {
    if (session) {
      window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
      refreshGames(session.authToken);
      return;
    }

    window.localStorage.removeItem(SESSION_KEY);
    setGames([]);
  }, [session]);

  useEffect(() => () => {
    disconnectSocket();
  }, []);

  function disconnectSocket() {
    const socket = socketRef.current;
    if (!socket) {
      return;
    }

    socket.onopen = null;
    socket.onmessage = null;
    socket.onerror = null;
    socket.onclose = null;
    socket.close();
    socketRef.current = null;
  }

  async function refreshGames(authToken = session?.authToken) {
    if (!authToken) {
      return;
    }

    try {
      const response = await listGames(authToken);
      setGames(response?.games ?? []);
    } catch (err) {
      setError(err.message);
    }
  }

  function updateForm(group, field, value) {
    setForms((current) => ({
      ...current,
      [group]: {
        ...current[group],
        [field]: value,
      },
    }));
  }

  async function handleAuth(action) {
    setError('');
    setBanner('');
    setBusyAction(action);

    try {
      if (action === 'login') {
        const response = await loginUser(forms.login);
        setSession(response);
        setForms((current) => ({
          ...current,
          login: { username: '', password: '' },
        }));
      } else {
        const response = await registerUser(forms.register);
        setSession(response);
        setForms((current) => ({
          ...current,
          register: { username: '', password: '', email: '' },
        }));
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleLogout() {
    if (!session) {
      return;
    }

    setBusyAction('logout');
    setError('');

    try {
      disconnectSocket();
      await logoutUser(session.authToken);
      setActiveGame(null);
      setSelectedSquare(null);
      setSession(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleCreateGame(event) {
    event.preventDefault();
    if (!forms.create.gameName.trim() || !session) {
      return;
    }

    setBusyAction('create');
    setError('');
    setBanner('');

    try {
      await createGame(session.authToken, forms.create.gameName.trim());
      setForms((current) => ({
        ...current,
        create: { gameName: '' },
      }));
      setBanner('Game created');
      await refreshGames();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyAction('');
    }
  }

  async function handleJoinGame(gameID, playerColor) {
    if (!session) {
      return;
    }

    setBusyAction(`join-${gameID}-${playerColor}`);
    setError('');

    try {
      await joinGame(session.authToken, gameID, playerColor);
      await refreshGames();
      openGame(gameID, playerColor);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyAction('');
    }
  }

  function openGame(gameID, preferredColor = null) {
    if (!session) {
      return;
    }

    disconnectSocket();
    setError('');
    setBanner('');
    setSelectedSquare(null);
    setActiveGame({
      gameID,
      game: null,
      connectionState: 'connecting',
      messages: [],
      playerColor: preferredColor,
      orientationColor: preferredColor ?? 'WHITE',
    });

    const socket = new WebSocket(parseSocketUrl());
    socketRef.current = socket;

    socket.onopen = () => {
      socket.send(
        JSON.stringify({
          commandType: 'CONNECT',
          authToken: session.authToken,
          gameID,
        }),
      );
      setActiveGame((current) =>
        current && current.gameID === gameID
          ? { ...current, connectionState: 'connected' }
          : current,
      );
    };

    socket.onmessage = (event) => {
      const payload = JSON.parse(event.data);
      const message = normalizeMessage(payload);

      if (!message) {
        return;
      }

      setActiveGame((current) => {
        if (!current || current.gameID !== gameID) {
          return current;
        }

        if (message.type === 'LOAD_GAME') {
          return {
            ...current,
            game: message.game,
            orientationColor: current.playerColor ?? message.playerColor ?? 'WHITE',
          };
        }

        return {
          ...current,
          messages: [
            {
              kind: message.type,
              text: message.text,
              id: `${Date.now()}-${Math.random()}`,
            },
            ...current.messages,
          ].slice(0, 12),
        };
      });

      if (message.type === 'ERROR') {
        setError(message.text);
      }
    };

    socket.onerror = () => {
      setError('Websocket connection failed');
      setActiveGame((current) =>
        current && current.gameID === gameID
          ? { ...current, connectionState: 'error' }
          : current,
      );
    };

    socket.onclose = () => {
      setActiveGame((current) =>
        current && current.gameID === gameID
          ? { ...current, connectionState: 'closed' }
          : current,
      );
      socketRef.current = null;
    };
  }

  function sendCommand(command) {
    const socket = socketRef.current;
    if (!socket || socket.readyState !== WebSocket.OPEN) {
      setError('Websocket is not connected');
      return false;
    }

    socket.send(JSON.stringify(command));
    return true;
  }

  function handleSquareClick(row, col) {
    if (!activeGame?.game?.board?.squares || !activeGame.playerColor) {
      return;
    }

    const piece = activeGame.game.board.squares[row - 1][col - 1];

    if (!selectedSquare) {
      if (!piece || piece.pieceColor !== activeGame.playerColor) {
        return;
      }

      setSelectedSquare({ row, col });
      return;
    }

    if (selectedSquare.row === row && selectedSquare.col === col) {
      setSelectedSquare(null);
      return;
    }

    const sourcePiece = activeGame.game.board.squares[selectedSquare.row - 1][selectedSquare.col - 1];
    let promotionPiece = null;

    if (isPromotionMove(sourcePiece, row)) {
      const choice = window.prompt(
        'Promote to QUEEN, ROOK, BISHOP, or KNIGHT',
        'QUEEN',
      );

      if (!choice) {
        return;
      }

      const normalized = choice.trim().toUpperCase();
      if (!PROMOTION_OPTIONS.includes(normalized)) {
        setError('Invalid promotion choice');
        return;
      }

      promotionPiece = normalized;
    }

    const sent = sendCommand({
      commandType: 'MAKE_MOVE',
      authToken: session.authToken,
      gameID: activeGame.gameID,
      move: {
        startPosition: {
          row: selectedSquare.row,
          col: selectedSquare.col,
        },
        endPosition: {
          row,
          col,
        },
        promotionPiece,
      },
    });

    if (sent) {
      setSelectedSquare(null);
      setBanner(`Move sent: ${squareLabel(selectedSquare)} to ${squareLabel({ row, col })}`);
    }
  }

  function handleLeaveGame() {
    if (!activeGame || !session) {
      return;
    }

    sendCommand({
      commandType: 'LEAVE',
      authToken: session.authToken,
      gameID: activeGame.gameID,
    });

    window.setTimeout(() => {
      disconnectSocket();
      setActiveGame(null);
      setSelectedSquare(null);
      refreshGames();
    }, 100);
  }

  function handleResignGame() {
    if (!activeGame || !session) {
      return;
    }

    sendCommand({
      commandType: 'RESIGN',
      authToken: session.authToken,
      gameID: activeGame.gameID,
    });
  }

  const orientation = activeGame?.orientationColor ?? activeGame?.playerColor ?? 'WHITE';

  return (
    <div className="shell">
      <section className="hero">
        <div>
          <p className="eyebrow">React Frontend for Your Existing Chess Server</p>
          <h1>Castleline Chess</h1>
          <p className="lede">
            The UI uses your current REST routes and websocket contract without changing the Java backend.
          </p>
        </div>
        <div className="hero-card">
          <span className="hero-label">Server contract</span>
          <strong>POST /user</strong>
          <strong>POST /session</strong>
          <strong>GET/POST/PUT /game</strong>
          <strong>WS /ws</strong>
        </div>
      </section>

      {(banner || error) && (
        <section className="message-strip">
          {banner && <div className="message success">{banner}</div>}
          {error && <div className="message error">{error}</div>}
        </section>
      )}

      {!session ? (
        <section className="auth-grid">
          <form className="panel" onSubmit={(event) => {
            event.preventDefault();
            handleAuth('login');
          }}>
            <div className="panel-header">
              <span>01</span>
              <h2>Login</h2>
            </div>
            <label>
              Username
              <input
                value={forms.login.username}
                onChange={(event) => updateForm('login', 'username', event.target.value)}
                placeholder="player one"
              />
            </label>
            <label>
              Password
              <input
                type="password"
                value={forms.login.password}
                onChange={(event) => updateForm('login', 'password', event.target.value)}
                placeholder="password"
              />
            </label>
            <button type="submit" disabled={busyAction === 'login'}>
              {busyAction === 'login' ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <form className="panel" onSubmit={(event) => {
            event.preventDefault();
            handleAuth('register');
          }}>
            <div className="panel-header">
              <span>02</span>
              <h2>Register</h2>
            </div>
            <label>
              Username
              <input
                value={forms.register.username}
                onChange={(event) => updateForm('register', 'username', event.target.value)}
                placeholder="new player"
              />
            </label>
            <label>
              Password
              <input
                type="password"
                value={forms.register.password}
                onChange={(event) => updateForm('register', 'password', event.target.value)}
                placeholder="password"
              />
            </label>
            <label>
              Email
              <input
                type="email"
                value={forms.register.email}
                onChange={(event) => updateForm('register', 'email', event.target.value)}
                placeholder="you@example.com"
              />
            </label>
            <button type="submit" disabled={busyAction === 'register'}>
              {busyAction === 'register' ? 'Creating account...' : 'Create account'}
            </button>
          </form>
        </section>
      ) : (
        <section className="app-grid">
          <div className="stack">
            <section className="panel">
              <div className="panel-header">
                <span>Lobby</span>
                <h2>{session.username}</h2>
              </div>
              <div className="toolbar">
                <button type="button" onClick={() => refreshGames()} disabled={busyAction === 'refresh'}>
                  Refresh games
                </button>
                <button type="button" className="secondary" onClick={handleLogout} disabled={busyAction === 'logout'}>
                  {busyAction === 'logout' ? 'Signing out...' : 'Logout'}
                </button>
              </div>
              <form className="inline-form" onSubmit={handleCreateGame}>
                <input
                  value={forms.create.gameName}
                  onChange={(event) => updateForm('create', 'gameName', event.target.value)}
                  placeholder="New game name"
                />
                <button type="submit" disabled={busyAction === 'create'}>
                  {busyAction === 'create' ? 'Creating...' : 'Create'}
                </button>
              </form>
            </section>

            <section className="panel">
              <div className="panel-header">
                <span>Open Games</span>
                <h2>{games.length}</h2>
              </div>
              <div className="game-list">
                {games.length === 0 && <p className="muted">No games yet. Create one to start.</p>}
                {games.map((game) => (
                  <article className="game-card" key={game.gameID}>
                    <div>
                      <h3>{game.gameName}</h3>
                      <p>Game #{game.gameID}</p>
                    </div>
                    <div className="seats">
                      <span>White: {game.whiteUsername ?? 'Open'}</span>
                      <span>Black: {game.blackUsername ?? 'Open'}</span>
                    </div>
                    <div className="game-actions">
                      <button
                        type="button"
                        onClick={() => handleJoinGame(game.gameID, 'WHITE')}
                        disabled={Boolean(game.whiteUsername)}
                      >
                        Join White
                      </button>
                      <button
                        type="button"
                        onClick={() => handleJoinGame(game.gameID, 'BLACK')}
                        disabled={Boolean(game.blackUsername)}
                      >
                        Join Black
                      </button>
                      <button
                        type="button"
                        className="secondary"
                        onClick={() => openGame(game.gameID)}
                      >
                        Observe
                      </button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          </div>

          <section className="panel board-panel">
            <div className="panel-header">
              <span>{activeGame ? `Game #${activeGame.gameID}` : 'Board'}</span>
              <h2>{activeGame ? statusFromGame(activeGame.game) : 'Select a game'}</h2>
            </div>

            {!activeGame ? (
              <div className="empty-state">
                <p>Join a side or observe a game from the lobby.</p>
              </div>
            ) : (
              <>
                <div className="board-meta">
                  <span>Viewing as {activeGame.playerColor ?? 'OBSERVER'}</span>
                  <span>Socket {activeGame.connectionState}</span>
                </div>
                <div className="board-wrap">
                  <div className="file-labels top">
                    {boardCols(orientation).map((col) => (
                      <span key={`top-${col}`}>{FILES[col - 1]}</span>
                    ))}
                  </div>
                  <div className="board-grid-shell">
                    <div className="rank-labels">
                      {boardRows(orientation).map((row) => (
                        <span key={`left-${row}`}>{row}</span>
                      ))}
                    </div>
                    <div className="board-grid">
                      {boardRows(orientation).map((row) =>
                        boardCols(orientation).map((col) => {
                          const piece = activeGame.game?.board?.squares?.[row - 1]?.[col - 1] ?? null;
                          const isSelected =
                            selectedSquare?.row === row && selectedSquare?.col === col;

                          return (
                            <button
                              key={`${row}-${col}`}
                              type="button"
                              className={[
                                'square',
                                (row + col) % 2 === 0 ? 'light' : 'dark',
                                isSelected ? 'selected' : '',
                              ].join(' ')}
                              onClick={() => handleSquareClick(row, col)}
                            >
                              <span className="square-name">{FILES[col - 1]}{row}</span>
                              <span className="piece">{getPieceSymbol(piece)}</span>
                            </button>
                          );
                        }),
                      )}
                    </div>
                    <div className="rank-labels">
                      {boardRows(orientation).map((row) => (
                        <span key={`right-${row}`}>{row}</span>
                      ))}
                    </div>
                  </div>
                  <div className="file-labels bottom">
                    {boardCols(orientation).map((col) => (
                      <span key={`bottom-${col}`}>{FILES[col - 1]}</span>
                    ))}
                  </div>
                </div>

                <div className="toolbar">
                  <button type="button" className="secondary" onClick={handleLeaveGame}>
                    Leave game
                  </button>
                  <button
                    type="button"
                    className="danger"
                    onClick={handleResignGame}
                    disabled={!activeGame.playerColor}
                  >
                    Resign
                  </button>
                </div>

                <div className="panel inset">
                  <div className="panel-header compact">
                    <span>Feed</span>
                    <h3>Server messages</h3>
                  </div>
                  <div className="message-log">
                    {activeGame.messages.length === 0 && (
                      <p className="muted">Notifications and websocket errors will appear here.</p>
                    )}
                    {activeGame.messages.map((message) => (
                      <div className={`log-item ${message.kind.toLowerCase()}`} key={message.id}>
                        <strong>{message.kind}</strong>
                        <p>{message.text}</p>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            )}
          </section>
        </section>
      )}
    </div>
  );
}

export default App;
