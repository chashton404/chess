const jsonHeaders = {
  'Content-Type': 'application/json',
};

function buildErrorMessage(status, payload) {
  if (payload && typeof payload === 'object' && payload.message) {
    return payload.message;
  }

  return `Request failed with status ${status}`;
}

async function request(path, { method = 'GET', authToken, body } = {}) {
  const response = await fetch(path, {
    method,
    headers: {
      ...jsonHeaders,
      ...(authToken ? { authorization: authToken } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  const text = await response.text();
  const payload = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(buildErrorMessage(response.status, payload));
  }

  return payload;
}

export function registerUser(credentials) {
  return request('/user', {
    method: 'POST',
    body: credentials,
  });
}

export function loginUser(credentials) {
  return request('/session', {
    method: 'POST',
    body: credentials,
  });
}

export function logoutUser(authToken) {
  return request('/session', {
    method: 'DELETE',
    authToken,
  });
}

export function listGames(authToken) {
  return request('/game', {
    authToken,
  });
}

export function createGame(authToken, gameName) {
  return request('/game', {
    method: 'POST',
    authToken,
    body: { gameName },
  });
}

export function joinGame(authToken, gameID, playerColor) {
  return request('/game', {
    method: 'PUT',
    authToken,
    body: { gameID, playerColor },
  });
}
